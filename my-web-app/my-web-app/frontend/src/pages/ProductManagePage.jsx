import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';

const API_URL = 'http://localhost:8080/api/products';

function ProductManagePage() {
    const navigate = useNavigate(); //画面遷移用フック
    const [products, setProducts] = useState([]);
    const [loading, setLoading] = useState(true);

    const fetchProducts = async () => {
        setLoading(true);
        try {
            const response = await axios.get(API_URL);
            setProducts(response.data);
        } catch (error) {
            console.error("データの取得に失敗しました:", error);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchProducts();
    }, []);

    // 削除処理 (ProductListPageから移植)
    const handleDelete = async (productId, productName) => {
        if (!window.confirm(`「${productName}」を本当に削除しますか？`)) {
            return;
        }
        try {
            await axios.delete(`${API_URL}/${productId}`);
            alert(`「${productName}」を削除しました。`);
            fetchProducts(); 
        } catch (error) {
            console.error("削除に失敗しました:", error);
            alert("削除に失敗しました。サーバーを確認してください。");
        }
    };

    // 更新画面への遷移 (ProductListPageから移植)
    const handleEdit = (productId) => {
        navigate(`/add/${productId}`); 
    };
    
    // トップ画面への遷移
    const handleGoToTop = () => {
        navigate('/');
    };

    if (loading) {
        return <div className="text-center mt-5">商品データを読み込み中...</div>;
    }

    return (
        <div className="container mt-4">
            <h2 className="mb-4">商品管理画面</h2>
            
            <div className="d-flex justify-content-between mb-3">
                <button className="btn btn-secondary" onClick={handleGoToTop}>
                    &lt; トップ画面に戻る
                </button>
                
                <button className="btn btn-primary" onClick={() => navigate('/add')}>
                    + 商品追加
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
                                    className="btn btn-sm btn-warning"
                                    onClick={() => handleEdit(product.id)}
                                >
                                    更新
                                </button>
                                <button 
                                    className="btn btn-sm btn-danger ms-5"
                                    onClick={() => handleDelete(product.id, product.name)}
                                >
                                    削除
                                </button>
                            </td>
                        </tr>
                    ))}
                </tbody>
            </table>
        </div>
    );
}

export default ProductManagePage;