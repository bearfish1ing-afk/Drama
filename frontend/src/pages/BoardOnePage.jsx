import { useState, useMemo, useRef, useEffect } from "react";
import { useParams } from "react-router-dom";
import { fetchWithAccess } from "../util/fetchUtil";
import axios from "axios";
import "react-quill/dist/quill.snow.css";

const BACKEND_API_BASE_URL = import.meta.env.VITE_BACKEND_API_BASE_URL;

function BoardOnePage(){
    const {boardId}=useParams();
    const[board,setBoard]=useState(null);
    const [error, setError] = useState(false);

    useEffect(() => {
        const getBoardDetail = async () => {
            try {
                const url = `${BACKEND_API_BASE_URL}/board/${boardId}`;
                // 1. fetchWithAccess는 비동기 함수이므로 await를 붙여야 합니다.
                const response = await fetchWithAccess(url);
                const data = await response.json();
                
                setBoard(data);
            } catch (err) {
                console.error("데이터 로딩 실패", err);
            }
        };

        if (boardId) {
            getBoardDetail();
        }
    }, [boardId]);

    // 1. 에러 발생 시 처리
    if (error) return <div className="error">게시글을 불러오는 데 실패했습니다.</div>;

    // 2. 데이터가 아직 없을 때 (로딩 중) 처리 - 이게 없으면 하단 렌더링에서 에러 남
    if (!board) return <div className="loading">로딩 중...</div>;

    return(
        <div className="board-detail">
            <h1>{board.title}</h1>
            <img src={`https://image.tmdb.org/t/p/w200${board.poster}`} alt={board.title} />
            <h2>
                {board.tmdbTitle}
                <small>({board.tmdbTitle})</small>
            </h2>
            <div 
                className="contentOne-body"
                dangerouslySetInnerHTML={{ __html: board.content }} 
            />
        </div>
    );
}

export default BoardOnePage;
