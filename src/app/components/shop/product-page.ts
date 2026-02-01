

import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { Product } from '../../models/product';
import { UserRole, User } from '../../models/user';
import { ProductService } from '../../services/product';
import { AuthService } from '../../services/auth';
import { CartItem } from '../../models/cart-item';
import { CartService } from '../../services/cart';
import { ProductCustomizer } from './product-customizer/product-customizer';

type CustomerView = 'catalog' | 'customizer';

@Component({
  selector: 'app-product-page',
  standalone: true,
  imports: [CommonModule, FormsModule, ProductCustomizer],
  templateUrl: './product-page.html',
  styleUrls: ['./product-page.css']
})
export class ProductPage implements OnInit {
  products: Product[] = [];
  view: CustomerView = 'catalog';
  selectedProduct: Product | null = null;
  role: UserRole | null = null;

  // Catalog State
  searchTerm = '';
  selectedCategory = 'All';
  currentPage = 1;
  itemsPerPage = 12;

  constructor(
    private productService: ProductService,
    private authService: AuthService,
    private cartService: CartService,
    private route: ActivatedRoute
  ) { }

  ngOnInit(): void {
    this.role = this.authService.getCurrentUser()?.role || null;
    this.productService.getProducts().subscribe({
      next: p => this.products = p,
      error: () => console.error('Failed to load products')
    });

    this.route.queryParamMap.subscribe(params => {
      this.view = (params.get('view') === 'customizer') ? 'customizer' : 'catalog';
    });
  }

  // Catalog Logic
  get categories(): string[] {
    const set = new Set(this.products.map(p => p.category).filter((c): c is string => !!c));
    return ['All', ...Array.from(set)];
  }

  get filteredProducts(): Product[] {
    const term = this.searchTerm.trim().toLowerCase();

    let filtered = this.products.filter(p => {
      const matchesCategory = this.selectedCategory === 'All' || p.category === this.selectedCategory;
      const matchesSearch = !term || p.name.toLowerCase().includes(term) || (p.description?.toLowerCase().includes(term) ?? false);
      const isActive = p.isActive !== false; // Default to true if undefined
      return matchesCategory && matchesSearch && isActive;
    });

    return filtered.sort((a, b) => {
      const aStock = (a.stockLevel ?? 1) > 0;
      const bStock = (b.stockLevel ?? 1) > 0;
      return (aStock === bStock) ? 0 : (aStock ? -1 : 1);
    });
  }

  get paginatedProducts(): Product[] {
    const start = (this.currentPage - 1) * this.itemsPerPage;
    return this.filteredProducts.slice(start, start + this.itemsPerPage);
  }

  get totalPages(): number {
    return Math.ceil(this.filteredProducts.length / this.itemsPerPage);
  }

  setPage(page: number): void {
    if (page >= 1 && page <= this.totalPages) {
      this.currentPage = page;
      window.scrollTo({ top: 0, behavior: 'smooth' });
    }
  }

  onFilterChange(): void { this.currentPage = 1; }
  isOutOfStock(p: Product): boolean { return (p.stockLevel ?? 1) <= 0; }
  isStockAvailable(p: Product): boolean { return (p.stockLevel ?? 1) > 0; }

  handleSelectProduct(product: Product): void {
    if (this.role === 'CUSTOMER') {
      this.selectedProduct = product;
      this.view = 'customizer';
    }
  }

  handleBackToCatalog(): void {
    this.selectedProduct = null;
    this.view = 'catalog';
  }

  handleAddToCart(event: { product: Product; customization: any; price: number }): void {
    if (!this.isStockAvailable(event.product)) return;

    this.cartService.addToCart(event.product.productId!, 1, event.customization).subscribe({
      next: () => {
        console.log('Added to cart');
        this.handleBackToCatalog();
      },
      error: () => console.error('Failed to add to cart.')
    });
  }

  onImageError(event: Event): void {
    (event.target as HTMLImageElement).src = 'https://via.placeholder.com/300x200?text=Preview';
  }
}
