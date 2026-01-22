import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ProductCustomizer } from './product-customizer';

describe('ProductCustomizer', () => {
  let component: ProductCustomizer;
  let fixture: ComponentFixture<ProductCustomizer>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ProductCustomizer]
    })
      .compileComponents();

    fixture = TestBed.createComponent(ProductCustomizer);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should initialize with default options from product', () => {
    component.product = {
      productId: 'P123',
      name: 'Test Product',
      basePrice: 100,
      previewImage: '',
      customOptions: [
        {
          type: 'colour',
          options: [{ label: 'Red', priceModifier: 0 }, { label: 'Blue', priceModifier: 10 }]
        },
        {
          type: 'size',
          options: [{ label: 'M', priceModifier: 0 }]
        },
        {
          type: 'material',
          options: [{ label: 'Cotton', priceModifier: 0 }]
        }
      ]
    };
    fixture.detectChanges();

    expect(component.selectedColor).toBe('Red');
    expect(component.colourOptions.length).toBe(2);
    expect(component.colourOptions[1].priceModifier).toBe(10);
  });
});
