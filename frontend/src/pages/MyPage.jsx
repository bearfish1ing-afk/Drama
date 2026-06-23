import { useEffect, useState } from "react";
import { Link } from "react-router-dom";

// .env로 부터 백엔드 URL 받아오기
const BACKEND_API_BASE_URL = import.meta.env.VITE_BACKEND_API_BASE_URL;

function Mypage(){
    return(
        <div>
            <h1><Link to="/board">게시물</Link></h1>
            <h1><Link to="/search">게시물 생성</Link></h1>
            <h1><Link to="/friends">친구 목록</Link></h1>
        </div>
    );
}

export default Mypage;