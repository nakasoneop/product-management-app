import { BrowserRouter, Routes, Route, NavLink } from 'react-router-dom'; // 💡 追加
import ProductListPage from './pages/ProductListPage';
import ProductDetailPage from './pages/ProductDetailPage';
import AddProductPage from './pages/AddProductPage';
import CheckoutPage from './pages/CheckoutPage';
import ProductManagePage from './pages/ProductManagePage';
import OrderConfirmationPage from './pages/OrderConfirmationPage';

function App() {
  return (
    // 💡 BrowserRouterで全体を囲み、ルーティングを有効にする
    <BrowserRouter>
      <div className="container mt-4">
        <nav className="navbar navbar-expand-lg navbar-dark bg-primary mb-4 p-3 rounded">
          <div className="container-fluid">
            <NavLink className="navbar-brand" to="/">商品管理システム</NavLink>
            <div className="navbar-nav">
            </div>
          </div>
        </nav>
        <Routes>
          <Route path="/" element={<ProductListPage />} />
          <Route path="/manage" element={<ProductManagePage />} />
          <Route path="/products/:id" element={<ProductDetailPage />} />
          <Route path="/add/:id?" element={<AddProductPage />} />
          <Route path="/checkout" element={<CheckoutPage />} />
          <Route path="/confirm" element={<OrderConfirmationPage />} />
        </Routes>
      </div>
    </BrowserRouter>
  );
}

export default App;