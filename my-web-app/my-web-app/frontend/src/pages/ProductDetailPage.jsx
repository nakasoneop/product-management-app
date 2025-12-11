import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import axios from 'axios';

const API_BASE_URL = 'http://localhost:8080/api/products'; // ベースURL

function ProductDetailPage() {
	// URLの /products/:id の :id 部分の値を取得する
	const { id } = useParams();
	const [product, setProduct] = useState(null);
	const [loading, setLoading] = useState(true);
	const [error, setError] = useState(null);
	const navigate = useNavigate(); // 画面遷移用フック

	// 💡 セッションストレージ用のキーを定義
	const storageKey = `qty_product_${id}`;

	const [quantity, setQuantity] = useState(() => {
        // ページマウント時、まずセッションストレージに保存された値を確認する
        const savedQty = sessionStorage.getItem(storageKey);
        // あればその値を、なければデフォルトの '1' を返す
        return savedQty || '1';
    });

		const handleChangeQuantity = (e) => {
        const value = e.target.value;
        setQuantity(value);
        
        // 💡 入力があるたびにセッションストレージに保存
        sessionStorage.setItem(storageKey, value); 
    };

		const handleBuyClick = () => {
        // 💡 数量のバリデーション (必須)
				if (quantity <= 0) {
					alert("購入数量を1以上で入力してください。");
					return;
    		}
        else if (quantity > product.stock) {
            alert("在庫がありません。");
            return;
        }

        // 💡 CheckoutPage にデータを state として渡す
        navigate('/checkout', {
            state: {
                productId: product.id,
                productName: product.name,
                unitPrice: product.price,
                quantity: quantity,
								total: product.price * quantity
            }
        });
    };
  
	useEffect(() => {
		const fetchProductDetail = async () => {
			try {
				// 💡 API URLを組み立てる: /api/products/ + ID
				const response = await axios.get(`${API_BASE_URL}/${id}`);
				setProduct(response.data); // データをセット
				setError(null);
			} catch (err) {
				console.error("Failed to fetch product detail:", err);
				setError('商品詳細情報の取得に失敗しました。');
			} finally {
				setLoading(false); // ローディング終了
			}
		};

		// IDが存在する場合のみ実行
		if (id) {
			fetchProductDetail();
		}
  }, [id]); // 💡 [id] を依存配列に入れることで、IDが変わったときにも再実行される
	
		// 💡 レンダリングロジック
		if (loading) {
			return <div className="detail-container">詳細データを読み込み中です...</div>;
		}

		if (error) {
			return <div className="detail-container error">{error}</div>;
		}
		if (!product) {
			return <div className="detail-container">商品が見つかりません。</div>;
		}

    return (
        <div className="container mt-3">
            <div className="card p-4 mb-3 shadow">
                <h2 className="text-primary mb-3">{product.name}</h2>
                <hr />
                
                <div className="row mb-4">
									<div className="col-md-5 mb-3">
                        {product.imageUrl ? (
                            <img 
                                src={`http://localhost:8080${product.imageUrl}`}
                                alt={product.name} 
                                className="img-fluid border rounded" 
                                style={{ maxHeight: '200px', objectFit: 'contain' }}
                            />
                        ) : (
                            <div className="border p-5 bg-light text-center text-muted rounded" style={{ height: '200px' }}>
                                画像なし
                            </div>
                        )}
                    </div>

                    <div className="col-md-6">
                        <h4>価格: <span className="text-danger fw-bold">{product.price.toLocaleString()} 円</span></h4>
                        <p className={`fw-bold ${product.stock > 0 ? 'text-success' : 'text-danger'}`}>
                            在庫数: {product.stock > 0 ? `${product.stock}` : '在庫切れ'}
                        </p>
                    </div>
                </div>
                
                {/* 💡 商品の説明文 */}
                <div className="mb-4">
                    {/* 💡 Pre-wrap で改行を保持し、見やすく表示 */}
                    <p style={{ whiteSpace: 'pre-wrap' }}>
                        {product.description}
                    </p>
                </div>

                <hr />
                
                <div className="d-flex align-items-center mb-4">
                    <div className="mb-0 me-3 w-25"> 
                        <label className="form-label">数量</label>
                        <input 
                            type="number" 
                            className="form-control" 
                            value={quantity} 
                            min="1"
                            max={product.stock}
                            onChange={handleChangeQuantity}
                        />
                    </div>
                    
                    {/* 💡 ボタン群を横並びに配置 (display: flex を利用) */}
                    <div className="d-flex align-self-end">
                        <button 
                            className="btn btn-success me-2"
                            onClick={handleBuyClick}
                            disabled={product.stock === 0} 
                        >
                            {product.stock > 0 ? 'この商品を購入する' : '在庫切れ'}
                        </button>

                        <button 
                            className="btn btn-secondary ms-2" 
                            onClick={() => navigate('/')}
                        >
                            一覧に戻る
                        </button>
                    </div>
                </div>
            </div>
        </div>
    );
}

export default ProductDetailPage;