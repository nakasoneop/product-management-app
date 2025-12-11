import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import axios from 'axios';

const API_URL = 'http://localhost:8080/api/products';

function AddProductPage() {
    // URLからIDを取得
    const { id } = useParams(); // IDがあれば文字列、なければ undefined //URLパラメータからIDを取得（分割代入）
    const isEditMode = !!id;     // IDが存在するかどうかで編集モードを判定
    const navigate = useNavigate();
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);
    // ファイル入力用
    const [imageFile, setImageFile] = useState(null);
    // フォームの状態を管理
    const [formData, setFormData] = useState({
        name: '',
        price: '',
        stock: '',
        description: '',
        imageUrl: ''
    });

    // 編集モードの場合、既存の商品データを読み込む
    //第一引数がAPI呼び出し、第2引数がいつ実行するか
    useEffect(() => {
        if (isEditMode) {
            setLoading(true);
            axios.get(`${API_URL}/${id}`)
                .then(response => {
                    // 取得した数値を文字列に変換してフォームにセット（input type="text" のため）
                    setFormData({
                        name: response.data.name,
                        price: response.data.price.toString(),
                        stock: response.data.stock.toString(),
                        description: response.data.description || '',
                        imageUrl: response.data.imageUrl || ''
                    });
                    setLoading(false);
                })
                .catch(err => {
                    console.error("データの読み込みに失敗しました:", err);
                    setError("商品データの読み込みに失敗しました。");
                    setLoading(false);
                });
        }
    }, [id, isEditMode]); // IDが変わったときやモードが変わったときに実行

    // ファイル入力の変更ハンドラ
    const handleFileChange = (e) => {
        setImageFile(e.target.files[0]);
    };

    // フォーム入力値の変更ハンドラ
    const handleChange = (e) => {
        const { name, value } = e.target;
        //動的に更新
        setFormData(prev => ({ ...prev, [name]: value }));
    };

    // フォーム送信ハンドラ (POST と PUT の切り替え)
    const handleSubmit = async (e) => {
        e.preventDefault();
        setError(null);
        setLoading(true);

        try {
            let response;
            let message;

            const dataToSend = {
                // IDは更新時のみ含める
                //スプレッド構文。右側に来たオブジェクトのプロパティを、外側のオブジェクトの中に展開（コピー）します。
                ...(isEditMode && { id: id }),
                name: formData.name,
                price: parseFloat(formData.price),
                stock: parseInt(formData.stock, 10),
                description: formData.description,
                // 新規登録時には imageUrl は送信しないか、空にする
                imageUrl: isEditMode ? formData.imageUrl : ''
            };

            //ライブラリ（axiosなど）が自動的に設定してくれる
            const config = { headers: {} };

            // ----------------------------------------------------
            // 新規登録モード (ファイルを含まない、シンプルな JSON POST)
            // ----------------------------------------------------
            if (!isEditMode) {
                // まず商品データ（ファイルなし）を送信
                response = await axios.post(API_URL, dataToSend);
                const newProductId = response.data.id; // 💡 新しく発行された ID を取得
                const newProductName = response.data.name; // 💡 新しく発行された名前を取得

                // 2. 画像ファイルが選択されていれば、ファイルを個別にアップロードする
                if (imageFile) {
                    //画像をアップロードするためのFormDataを作成
                    const imageFormData = new FormData();
                    imageFormData.append('imageFile', imageFile);

                    // 新しいAPIエンドポイントへ POST
                    await axios.post(`${API_URL}/${newProductId}/image`, imageFormData, config);
                }

                message = `新規商品「${newProductName}」を登録しました`;
            // ----------------------------------------------------
            // 更新モード (ファイルアップロードの可能性がある、Multipart POST)
            // ----------------------------------------------------
            } else { 
                // ファイルを扱うため、FormDataを使用
                const data = new FormData();
                
                // 商品データを Blob として追加 (サーバー側で @RequestPart("productDetails") に対応)
                //JSONデータと画像データを一緒に送信するために、JSONデータをBlobに変換してFormDataに追加
                const productDetailsBlob = new Blob([JSON.stringify(dataToSend)], {
                    type: 'application/json'
                });
                data.append('productDetails', productDetailsBlob);

                // ファイルがあれば追加 (サーバー側で @RequestPart("imageFile") に対応)
                if (imageFile) {
                    data.append('imageFile', imageFile);
                }

                // サーバーの /api/products/{id}/update パスに向けて POST
                response = await axios.post(`${API_URL}/${id}/update`, data, config);
                message = `「${response.data.name}」を更新しました`;
            }

            alert(message);
            navigate('/');

        } catch (err) {
            console.error("処理中にエラーが発生しました:", err);
            setError(isEditMode ? "更新に失敗しました。" : "登録に失敗しました。");
        } finally {
            setLoading(false);
        }
    };

    if (loading) {
        return <div className="text-center mt-5">データ読み込み中...</div>;
    }
    
    // --- 💡 レンダリング部分の修正 ---
    return (
        <div className="container mt-4">
            <h2 className="mb-4">
                {isEditMode ? `商品編集 (ID: ${id})` : '商品追加'}
            </h2>
            
            {error && <div className="alert alert-danger">{error}</div>}

            <form onSubmit={handleSubmit}>
                <div className="mb-3">
                    <label className="form-label">商品名</label>
                    <input 
                        type="text" 
                        className="form-control" 
                        name="name" 
                        value={formData.name} 
                        onChange={handleChange} 
                        required 
                    />
                </div>
                
                <div className="mb-3">
                    <label className="form-label">価格 (円)</label>
                    <input 
                        type="number" 
                        className="form-control" 
                        name="price" 
                        value={formData.price} 
                        onChange={handleChange} 
                        min="0"
                        step="0.01"
                        required 
                    />
                </div>
                
                <div className="mb-3">
                    <label className="form-label">在庫数</label>
                    <input 
                        type="number" 
                        className="form-control" 
                        name="stock" 
                        value={formData.stock} 
                        onChange={handleChange} 
                        min="0"
                        required 
                    />
                </div>

                <div className="mb-3">
                    <label className="form-label">商品説明</label>
                    <textarea 
                        className="form-control" 
                        name="description"
                        rows="3"           // 💡 縦の行数を指定
                        value={formData.description} 
                        onChange={handleChange} 
                    ></textarea>
                </div>

                <div className="mb-4">
                    <label className="form-label">商品画像</label>
                    <input 
                        type="file" 
                        className="form-control" 
                        name="imageFile" 
                        accept="image/*" // 画像ファイルのみ選択可能にする
                        onChange={handleFileChange} 
                    />
                </div>

                <button type="submit" className="btn btn-success" disabled={loading}>
                    {loading 
                        ? (isEditMode ? '更新中...' : '登録中...')
                        : (isEditMode ? '更新' : '登録')}
                </button>
                
                <button type="button" className="btn btn-secondary ms-3" onClick={() => navigate('/manage')}>
                    キャンセル
                </button>
            </form>
        </div>
    );
}

export default AddProductPage;