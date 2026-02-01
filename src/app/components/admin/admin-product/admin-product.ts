
import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { ProductService } from '../../../services/product';
import { Product } from '../../../models/product';

@Component({
    selector: 'app-admin-product',
    standalone: true,
    imports: [CommonModule, FormsModule],
    templateUrl: './admin-product.html',
    styleUrls: ['./admin-product.css']
})
export class AdminProduct implements OnInit {
    products: Product[] = [];
    filteredProducts: Product[] = [];
    searchQuery = '';

    // Editing state
    editingProduct: Product | null = null;
    editName = '';
    editDescription = '';
    editCategory = '';
    editPrice = 0;
    editActive = true;

    constructor(private productService: ProductService, private router: Router) { }

    ngOnInit(): void {
        this.refreshData();
    }

    refreshData(): void {
        this.productService.getProducts().subscribe({
            next: (products) => {
                this.products = products;
                this.filterProducts();
            },
            error: (err) => console.error('Error fetching products', err)
        });
    }

    navigateToAddProduct(): void {
        this.router.navigate(['/admin/add-product']);
    }

    navigateToCustomize(): void {
        this.router.navigate(['/admin/customize']);
    }

    onSearchChange(): void {
        this.filterProducts();
    }

    private filterProducts(): void {
        if (!this.searchQuery.trim()) {
            this.filteredProducts = [...this.products];
        } else {
            const query = this.searchQuery.toLowerCase();
            this.filteredProducts = this.products.filter(p =>
                p.name.toLowerCase().includes(query) ||
                p.category?.toLowerCase().includes(query)
            );
        }
    }

    // --- Actions ---

    deleteProduct(product: Product): void {
        if (!confirm(`Are you sure you want to delete "${product.name}"?`)) return;

        this.productService.deleteProduct(product.productId).subscribe({
            next: () => {
                alert('Product deleted');
                this.refreshData();
            },
            error: () => alert('Failed to delete product')
        });
    }

    openEditModal(product: Product): void {
        this.editingProduct = product;
        this.editName = product.name;
        this.editDescription = product.description || '';
        this.editCategory = product.category || '';
        this.editPrice = product.basePrice;
        this.editActive = product.isActive !== false;
    }

    closeEditModal(): void {
        this.editingProduct = null;
    }

    saveChanges(): void {
        if (!this.editingProduct) return;

        const updatedProduct: Product = {
            ...this.editingProduct,
            name: this.editName,
            description: this.editDescription,
            category: this.editCategory,
            basePrice: this.editPrice,
            isActive: this.editActive
        };

        this.productService.updateProduct(updatedProduct).subscribe({
            next: () => {
                alert('Product updated successfully');
                this.refreshData();
                this.closeEditModal();
            },
            error: () => alert('Failed to update product')
        });
    }
}
