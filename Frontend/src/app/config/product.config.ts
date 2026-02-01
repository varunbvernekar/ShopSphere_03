import { CustomOptionGroup } from '../models/product';

export const DEFAULT_CUSTOM_OPTIONS: CustomOptionGroup[] = [
    {
        type: 'colour',
        options: [
            { label: 'Silver', priceModifier: 0 },
            { label: 'Gold', priceModifier: 15 },
            { label: 'Rose Gold', priceModifier: 10 }
        ]
    },
    {
        type: 'size',
        options: [
            { label: 'Small', priceModifier: -5 },
            { label: 'Medium', priceModifier: 0 },
            { label: 'Large', priceModifier: 10 }
        ]
    },
    {
        type: 'material',
        options: [
            { label: 'Standard', priceModifier: 0 },
            { label: 'Premium', priceModifier: 25 }
        ]
    }
];
