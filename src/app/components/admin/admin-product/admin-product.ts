
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

        // We only update the fields we are editing.
        // Note: We are preserving the existing customOptions and image since we don't edit them here
        // based on "no images" requirement for the table, but the user didn't explicitly say "no image editing".
        // I will include image editing in the modal if I want to be thorough, but maybe just stick to the text fields as implied by the simplified requirements.
        // The user said "show all the products without any images as a table format".
        // I will stick to text-based editing to keep it clean, unless I should allow image text URL editing?
        // I'll leave image editing out for simplicity unless strictly needed, updating Name/Category/Price/Desc is usually key.
        // Actually, I'll include the Image URL field just in case they want to fix a broken link, but no file upload complexity.

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
