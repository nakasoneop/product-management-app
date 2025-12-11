import React, { useState } from 'react';
import { Link, useNavigate, useLocation } from 'react-router-dom'; // useNavigateをインポート
import axios from 'axios';

const API_URL = 'http://localhost:8080/api/orders'; // 注文API

function CheckoutPage() {
    const navigate = useNavigate(); //画面遷移用フック
    const location = useLocation(); // 💡 データの受け取り
    const [isOrdering, setIsOrdering] = useState(false); // 注文状態を管理するフック

    // 💡 渡されたデータを受け取る
    const orderDataFromState = location.state;

    // 💡 データがない場合はエラーまたはリダイレクト
    if (!orderDataFromState || !orderDataFromState.productId) {
        // データがない場合は一覧に戻す
        return <div className="alert alert-danger">注文情報が見つかりません。
            <Link to="/" className="alert-link">商品一覧へ戻る</Link>
        </div>;
    }

    const { productId, productName, unitPrice, quantity } = orderDataFromState; //分割代入
    const totalAmount = unitPrice * quantity;

    // 💡 バックエンドに送る最小限のデータ
    const orderDataToSend = {
        productId: productId, 
        productName: productName, 
        quantity: quantity ,
        unitPrice: unitPrice,
        total: totalAmount
    };
    
    const handleOrderSubmit = async () => {
        setIsOrdering(true);
        try {           
            // 💡 POSTリクエストの送信
            const response = await axios.post(API_URL, orderDataToSend);

            // セッションストレージから数量を削除（クリーンアップ）
            if (productId) {
                const storageKey = `qty_product_${productId}`;
                sessionStorage.removeItem(storageKey);
            }

            // 注文完了画面へ遷移
            navigate('/confirm', {
                state: {
                    orderId: response.data.id, // サーバーから返された注文ID
                    productId: productId,
                    productName: productName,
                    totalAmount: totalAmount
                }
            });

        } catch (error) {
            // ... (エラー処理は省略) ...
        } finally {
            setIsOrdering(false);
        }
    };

    // 💡 戻る処理を定義
    const handleBackClick = () => {
        // -1 を渡すことで、ブラウザ履歴を一つ戻る（前ページへ遷移）
        navigate(-1); 
    };

    return (
        <div className="container card p-4 shadow">
            <h2 className="mb-4 text-primary">🛒 注文内容の確認</h2>
            <p>以下の内容で注文を確定します。</p>
            
            <table className="table table-bordered w-75 mb-4">
                <tbody>
                    <tr><th>商品名</th><td>{productName}</td></tr>
                    <tr><th>単価</th><td>{unitPrice.toLocaleString()} 円</td></tr>
                    <tr><th>数量</th><td>{quantity}</td></tr>
                    <tr><th>合計金額</th><td className="fw-bold text-danger">{totalAmount.toLocaleString()} 円</td></tr>
                </tbody>
            </table>

            <div className="d-flex justify-content-between mt-4"> 
                <button 
                    className="btn btn-secondary me-2"
                    onClick={handleBackClick}
                    disabled={isOrdering}
                >
                    &lt; 前ページに戻る
                </button>
                
                <div className="d-flex">
                    <button 
                        className="btn btn-success" 
                        onClick={handleOrderSubmit}
                        disabled={isOrdering}
                    >
                        {isOrdering ? '注文処理中...' : `注文を確定する`}
                    </button>
                </div>
            </div>
        </div>
    );
}

export default CheckoutPage;