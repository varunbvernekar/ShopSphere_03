import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';

import { CartService } from '../../../services/cart';
import { Cart } from '../../../models/cart';

@Component({
    selector: 'app-cart-page',
    standalone: true,
    imports: [CommonModule, RouterModule],
    templateUrl: './cart-page.html',
    styleUrls: ['./cart-page.css']
})
export class CartPage implements OnInit {
    cart: Cart | null = null;
    constructor(public cartService: CartService,
        private router: Router) { }

    ngOnInit(): void {

        this.loadCart();
    }

    loadCart(): void {
        this.cartService.getCart().subscribe({
            next: (c) => this.cart = c,
            error: () => console.error('Failed to load cart')
        });
    }


    get items() { return this.cart?.items || []; }
    get hasItems() { return this.items.length > 0; }
    get subtotal() { return this.cart?.subtotal || 0; }
    get tax() { return this.cart?.tax || 0; }
    get shipping() { return this.cart?.shipping || 0; }
    get total() { return this.cart?.totalAmount || 0; }

    updateQty(itemId: string, currentQty: number, change: number): void {
        const newQty = currentQty + change;
        if (newQty > 0) {
            this.cartService.updateCartItem(itemId, newQty).subscribe({
                next: (c) => this.cart = c,
                error: () => console.error('Failed to update quantity')
            });
        }
    }

    removeItem(itemId: string): void {
        if (!confirm('Remove this item?')) return;
        this.cartService.removeItem(itemId).subscribe({
            next: (c) => this.cart = c,
            error: () => console.error('Failed to remove item')
        });
    }

    onCheckout(): void {
        if (!this.hasItems) return console.log('Your cart is empty.');
        this.router.navigate(['/payment']);
    }

    onImageError(event: Event): void {
        (event.target as HTMLImageElement).src = 'https://via.placeholder.com/150?text=No+Image';
    }
}
