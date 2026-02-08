
import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { ProductService } from '../../../services/product';

@Component({
    selector: 'app-admin-add-product',
    standalone: true,
    imports: [CommonModule, FormsModule],
    templateUrl: './add-product.html',
    styleUrls: ['./add-product.css']
})
export class AdminAddProduct {
    newProduct = {
        name: '', description: '', category: '',
        basePrice: 0, stockLevel: 0, reorderThreshold: 0, previewImage: ''
    };

    selectedFile: File | null = null;
    selectedFileName = '';
    imagePreview = '';
    showSuccessMessage = false;

    constructor(private productService: ProductService, private router: Router) { }

    onFileSelected(event: any): void {
        const file = event.target.files[0];
        if (file) {
            this.selectedFile = file;
            this.selectedFileName = file.name;
            const reader = new FileReader();
            reader.onload = (e: any) => this.imagePreview = e.target.result;
            reader.readAsDataURL(file);
        }
    }

    onAddProduct(): void {
        if (!this.newProduct.name || !this.newProduct.basePrice || !this.newProduct.category) return;

        const payload = {
            ...this.newProduct,
            basePrice: Number(this.newProduct.basePrice),
            stockLevel: Number(this.newProduct.stockLevel),
            reorderThreshold: Number(this.newProduct.reorderThreshold),
            customOptions: [], // Using empty options 
            isActive: true
        };

        this.productService.addProduct(payload, this.selectedFile || undefined).subscribe({
            next: () => {
                this.showSuccessMessage = true;
                this.resetForm();
                setTimeout(() => {
                    this.showSuccessMessage = false;
                    this.router.navigate(['/admin/inventory']);
                }, 2000);
            },
            error: () => alert('Failed to add product')
        });
    }

    private resetForm(): void {
        this.newProduct = {
            name: '', description: '', category: '',
            basePrice: 0, stockLevel: 0, reorderThreshold: 0, previewImage: ''
        };
        this.selectedFileName = '';
        this.imagePreview = '';
        this.selectedFile = null;

        const input = document.getElementById('productImage') as HTMLInputElement;
        if (input) input.value = '';
    }
}
