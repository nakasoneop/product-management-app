import { useNavigate, useLocation } from 'react-router-dom';

function OrderConfirmationPage() {
    const navigate = useNavigate();
    const location = useLocation();
    
    // 💡 注文データを state から受け取る
    const orderDetails = location.state || {};
    const { orderId, productName, totalAmount } = orderDetails;

    // データがない場合
    if (!orderId) {
        return (
            <div className="container mt-5 text-center">
                <div className="alert alert-warning">
                    注文情報が確認できませんでした。
                    <button className="btn btn-link" onClick={() => navigate('/')}>
                        トップに戻る
                    </button>
                </div>
            </div>
        );
    }

    return (
        <div className="container mt-5 text-center">
            <div className="card p-5 shadow-lg border-success">
                <h2 className="text-success mb-4">
                    ご注文が完了しました！
                </h2>
                <p className="lead">
                    商品名: <span className="fw-bold">{productName}</span> のご注文を承りました。
                </p>
                
                <table className="table table-sm w-50 mx-auto my-4">
                    <tbody>
                        <tr><th>注文番号</th><td>{orderId}</td></tr>
                        <tr><th>合計金額</th><td className="fw-bold text-danger">{totalAmount.toLocaleString()} 円</td></tr>
                    </tbody>
                </table>
                
                <p className="text-muted">
                    この度はご利用いただき、誠にありがとうございました。
                </p>
                
                <hr className="my-4" />
                
                <button 
                    className="btn btn-primary mt-3 mx-auto d-block"
                    onClick={() => navigate('/')}
                >
                    商品一覧に戻る
                </button>
            </div>
        </div>
    );
}

export default OrderConfirmationPage;