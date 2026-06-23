import { Link } from "react-router-dom";
import { useEffect, useState } from "react";
// .env로 부터 백엔드 URL 받아오기
const BACKEND_API_BASE_URL = import.meta.env.VITE_BACKEND_API_BASE_URL;

function HomePage(){
    const[userId,setUserId]=useState(null);
    
    useEffect(()=>{
        const savedId=localStorage.getItem("userId");
        if(savedId){
            setUserId(savedId);
        }
    },[]);

    return(
        <div>
            {userId ? (
            <h2>{userId}님, 환영합니다!</h2>
            ) : (
                <h1>로그인이 필요합니다.</h1>
            )}

            <hr/>
            <h1><Link to="/login">로그인</Link></h1>
            <h1><Link to="/signup">회원가입</Link></h1>
            <h1><Link to="/mypage">마이페이지</Link></h1>
            <h1><Link to="/id/search">친구 검색</Link></h1>
        </div>
    );
}

export default HomePage;