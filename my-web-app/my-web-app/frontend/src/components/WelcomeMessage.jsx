// 💡 .jsx または .js 拡張子を使う
import React from 'react';

// 💡 関数名をファイル名と同じにし、大文字で始める（Reactの慣例）
function WelcomeMessage() {
  const username = "User"; // JavaScriptのロジック

  return (
    // 💡 HTMLのようなタグを記述する（これがJSX）
    <div>
      <h1>こんにちは、{username}さん！</h1>
      <p>バックエンドAPIと連携する準備ができました。</p>
    </div>
  );
}

// 💡 このファイルを他の場所で使えるようにエクスポートする
export default WelcomeMessage;