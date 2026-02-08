export interface InventoryItem {
    productId: string;
    productName: string;
    productPrice: number;
    quantity: number;
    reorderThreshold: number;
}
