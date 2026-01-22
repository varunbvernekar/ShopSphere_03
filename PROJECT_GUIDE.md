# ShopSphere Project Guide

Welcome to the ShopSphere project! This guide is designed to help you understand the architecture, codebase structure, and core workflows of the application.

## 🏗️ Architecture Overview

ShopSphere is a modern e-commerce platform built with **Angular (v19)** and uses **json-server** as a mock backend.

- **Frontend**: Angular framework for building a responsive and dynamic user interface.
- **Backend**: A mock REST API powered by `json-server`, which uses `src/db.json` as its database.
- **Styling**: Vanilla CSS for styling, following a premium and modern design aesthetic.

## 📂 Directory Structure

Here's a breakdown of the main directories in the `src/app` folder:

- [**`components/`**](file:///Users/sowrava/Developer/Shopsphere/ShopSphere/src/app/components): Contains all the visual components of the application.
  - `auth/`: Login and Registration pages.
  - `shop/`: Product listing and Shopping Cart.
  - `admin/`: Inventory management and product customization dashboards.
  - `orders/`: Order tracking and history.
  - `profile/`: User profile management.
- [**`services/`**](file:///Users/sowrava/Developer/Shopsphere/ShopSphere/src/app/services): Contains the business logic and API interaction code.
  - [auth.ts](file:///Users/sowrava/Developer/Shopsphere/ShopSphere/src/app/services/auth.ts): Handles user session, login, and registration.
  - [product.ts](file:///Users/sowrava/Developer/Shopsphere/ShopSphere/src/app/services/product.ts): Manages product fetching and CRUD operations.
- [**`models/`**](file:///Users/sowrava/Developer/Shopsphere/ShopSphere/src/app/models): TypeScript interfaces for data models (e.g., `User`, `Product`, `Order`).
- [**`guards/`**](file:///Users/sowrava/Developer/Shopsphere/ShopSphere/src/app/guards): Route guards to protect pages based on authentication or user roles (Admin).
- [**`interceptors/`**](file:///Users/sowrava/Developer/Shopsphere/ShopSphere/src/app/interceptors): HTTP interceptors for adding auth tokens to requests.

## 🛤️ Core Workflows

### 🔐 Authentication & Registration
1.  **Register**: Users sign up via the `Register` component, which sends data to the `AuthService`. The service checks if the email exists in `db.json` and adds the new user if not.
2.  **Login**: Users log in via the `Login` component. `AuthService` validates credentials against `db.json` and generates a mock JWT token stored in `localStorage`.
3.  **Guards**: The `authGuard` and `adminGuard` ensure users can only access appropriate pages.

### 🛒 Shopping Flow
1.  **Browse Products**: The `ProductPage` component fetches products from `ProductService`.
2.  **Cart Management**: The `CartService` manages items in the cart, calculating totals and handling additions/removals.
3.  **Payment**: Users provide payment details (mocked) and complete orders, which are then saved to `db.json`.

### 🛠️ Admin Dashboard
1.  **Inventory**: Admins can view stock levels, update stock, and manage product availability in the `AdminInventory` component.
2.  **Product Customization**: Admins can customize default options for products (colors, sizes, materials).

## 🚀 How to Run

1.  **Start the Mock Backend**:
    ```bash
    npm run server
    ```
    This will start `json-server` on [http://localhost:3000](http://localhost:3000).

2.  **Start the Frontend**:
    ```bash
    npm start
    ```
    Open [http://localhost:4200](http://localhost:4200) in your browser.

## 📄 Key Files to Note
- [src/app/app.routes.ts](file:///Users/sowrava/Developer/Shopsphere/ShopSphere/src/app/app.routes.ts): The main navigation setup.
- [src/db.json](file:///Users/sowrava/Developer/Shopsphere/ShopSphere/src/db.json): The central data store for the application.
- [src/app/app.ts](file:///Users/sowrava/Developer/Shopsphere/ShopSphere/src/app/app.ts): The root component of the application.
