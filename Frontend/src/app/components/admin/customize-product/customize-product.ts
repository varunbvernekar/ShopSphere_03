import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Product, CustomOptionGroup, CustomOptionType } from '../../../models/product';
import { ProductService } from '../../../services/product';

@Component({
    selector: 'app-customize-product',
    standalone: true,
    imports: [CommonModule, FormsModule],
    templateUrl: './customize-product.html',
    styleUrls: ['./customize-product.css']
})
export class CustomizeProduct implements OnInit {
    products: Product[] = [];
    currentPage = 1;
    itemsPerPage = 16;

    selectedProduct: Product | null = null;
    editingOptions: CustomOptionGroup[] = [];
    newOptionType: CustomOptionType = 'colour';
    newOptionValue = '';
    newOptionPrice = 0;

    constructor(private productService: ProductService) { }

    ngOnInit(): void {
        this.loadProducts();
    }

    loadProducts(): void {
        this.productService.getProducts().subscribe({
            next: (data) => this.products = data,
            error: () => console.error('Failed to load products')
        });
    }


    get paginatedProducts(): Product[] {
        const start = (this.currentPage - 1) * this.itemsPerPage;
        return this.products.slice(start, start + this.itemsPerPage);
    }

    get totalPages(): number {
        return Math.ceil(this.products.length / this.itemsPerPage);
    }

    setPage(page: number): void {
        if (page >= 1 && page <= this.totalPages) {
            this.currentPage = page;
            window.scrollTo({ top: 0, behavior: 'smooth' });
        }
    }

    // Editor Actions
    openEditor(product: Product): void {
        this.selectedProduct = product;
        this.editingOptions = JSON.parse(JSON.stringify(product.customOptions || []));
    }

    closeEditor(): void {
        this.selectedProduct = null;
        this.editingOptions = [];
    }

    addOptionValue(): void {
        if (!this.newOptionValue.trim()) return;

        let group = this.editingOptions.find(o => o.type === this.newOptionType);
        if (!group) {
            group = { type: this.newOptionType, options: [] };
            this.editingOptions.push(group);
        }

        if (!group.options.some(o => o.label === this.newOptionValue)) {
            group.options.push({ label: this.newOptionValue, priceModifier: this.newOptionPrice || 0 });
        }

        this.newOptionValue = '';
        this.newOptionPrice = 0;
    }

    removeOptionValue(type: CustomOptionType, label: string): void {
        const group = this.editingOptions.find(o => o.type === type);
        if (group) {
            group.options = group.options.filter(o => o.label !== label);
        }
    }

    saveOptions(): void {
        if (!this.selectedProduct) return;

        const updated: Product = { ...this.selectedProduct, customOptions: this.editingOptions };
        this.productService.updateProduct(updated).subscribe({
            next: () => {
                alert('Saved successfully!');
                this.loadProducts();
                this.closeEditor();
            },
            error: () => alert('Failed to save options')
        });
    }
}
