// src/pages/ProductListPage.jsx

import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom'; // useNavigate をインポート
import axios from 'axios';

const API_URL = 'http://localhost:8080/api/products';

function ProductListPage() {
    const navigate = useNavigate();
    const [products, setProducts] = useState([]);

    // 💡 商品リスト取得関数
    const fetchProducts = async () => {
        try {
            const response = await axios.get(API_URL);
            setProducts(response.data);
        } catch (error) {
            console.error("データの取得に失敗しました:", error);
        }
    };

    useEffect(() => {
        fetchProducts();
    }, []);

    // 削除処理
    const handleDelete = async (productId) => {
        if (!window.confirm(`商品ID ${productId} を本当に削除しますか？`)) {
            return;
        }

        try {
            // DELETE API の呼び出し
            await axios.delete(`${API_URL}/${productId}`);
            alert(`商品ID ${productId} を削除しました。`);
            
            // 削除後、リストを再読み込み
            fetchProducts(); 

        } catch (error) {
            console.error("削除に失敗しました:", error);
            alert("削除に失敗しました。サーバーを確認してください。");
        }
    };

    // 更新画面への遷移
    const handleEdit = (productId) => {
        // 既存の /add ルートを編集画面として流用するため、ID を含めて遷移
        navigate(`/add/${productId}`); // 💡 例: /add/1 へ遷移
    };

    // --- 💡 レンダリング部分 ---
    return (
        <div className="container mt-4">
            <h2 className="mb-2">商品一覧</h2>
            
            <div className="d-flex justify-content-end mb-3">
                {/* 💡 商品管理画面へのリンク */}
                <button className="btn btn-outline-secondary me-2" onClick={() => navigate('/manage')}>
                    商品管理画面へ
                </button>
            </div>
            
            <table className="table table-striped">
                <thead>
                    <tr>
                        <th className="ps-3 text-center">ID</th>
                        <th className="text-center">商品名</th>
                        <th className="text-center">価格</th>
                        <th className="text-center">在庫数</th>
                        <th></th>
                    </tr>
                </thead>
                <tbody>
                    {products.map(product => (
                        <tr key={product.id}>
                            <td className="ps-3 text-center">{product.id}</td>
                            <td className="text-center">{product.name}</td>
                            <td className="text-center">{product.price.toLocaleString()}</td>
                            <td className="text-center">{product.stock}</td>
                            <td>
                                <button 
                                    // 在庫に応じてクラスを切り替える
                                    className={`btn btn-sm ${product.stock > 0 ? 'btn-success' : 'btn-secondary'}`}
                                    onClick={() => navigate(`/products/${product.id}`)}
                                    disabled={product.stock === 0} 
                                >
                                    {product.stock > 0 ? '購入' : '在庫切れ'}
                                </button>
                            </td>
                        </tr>
                    ))}
                </tbody>
            </table>
        </div>
    );
  }

export default ProductListPage;