import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Product } from '../../../models/product';

@Component({
  selector: 'app-product-customizer',
  standalone: true,
  imports: [CommonModule],
  styleUrl: './product-customizer.css',
  templateUrl: './product-customizer.html'
})
export class ProductCustomizer implements OnInit {
  @Input() product!: Product;
  @Output() addToCart = new EventEmitter<{ product: Product; customization: any; price: number }>();
  @Output() back = new EventEmitter<void>();

  selectedColor = '';
  selectedSize = '';
  selectedMaterial = '';
  totalPrice = 0;
  showSuccess = false;

  get colourOptions() { return this.getOptions('colour'); }
  get sizeOptions() { return this.getOptions('size'); }
  get materialOptions() { return this.getOptions('material'); }
  get isInStock() { return (this.product.stockLevel ?? 1) > 0; }

  ngOnInit(): void {
    if (this.product) {
      this.selectedColor = this.colourOptions[0]?.label || '';
      this.selectedSize = this.sizeOptions[0]?.label || '';
      this.selectedMaterial = this.materialOptions[0]?.label || '';
      this.recalculatePrice();
    }
  }

  private getOptions(type: string) {
    return this.product?.customOptions.find(o => o.type === type)?.options || [];
  }

  selectOption(type: 'colour' | 'size' | 'material', value: string): void {
    if (type === 'colour') this.selectedColor = value;
    if (type === 'size') this.selectedSize = value;
    if (type === 'material') this.selectedMaterial = value;
    this.recalculatePrice();
  }

  private recalculatePrice(): void {
    if (!this.product) return;
    let price = this.product.basePrice;

    [
      { type: 'colour', val: this.selectedColor },
      { type: 'size', val: this.selectedSize },
      { type: 'material', val: this.selectedMaterial }
    ].forEach(sel => {
      const option = this.product.customOptions.find(g => g.type === sel.type)
        ?.options.find(o => o.label === sel.val);
      if (option) price += option.priceModifier;
    });

    this.totalPrice = Number(price.toFixed(2));
  }

  handleAddToCart(): void {
    if (this.product.stockLevel === 0) {
      console.log(`${this.product.name} is out of stock.`);
      return;
    }

    this.addToCart.emit({
      product: this.product,
      customization: { color: this.selectedColor, size: this.selectedSize, material: this.selectedMaterial },
      price: this.totalPrice
    });

    this.showSuccess = true;
    setTimeout(() => (this.showSuccess = false), 2000);
  }
}
