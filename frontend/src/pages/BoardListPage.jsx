import { useState, useMemo, useRef, useEffect, useCallback } from "react";
import { useNavigate } from "react-router-dom";
import { fetchWithAccess } from "../util/fetchUtil";
import axios from "axios";
import "react-quill/dist/quill.snow.css";

const BACKEND_API_BASE_URL = import.meta.env.VITE_BACKEND_API_BASE_URL;

function BoardListPage(){
    const[boards,setBoards]=useState([]);
    const[page,setPage]=useState(0);
    const[hasNext,setHasNext]=useState(true);
    const[loading,setLoading]=useState(false);
    const navigate=useNavigate();
    const observer=useRef();

    const lastBoardElementRef=useCallback(node=>{
        if(loading)return;//지금 데이터를 불러오는 중이라면, 중복해서 다음 페이지를 요청하지 않도록 입구 컷
        if(observer.current)observer.current.disconnect();//이전의 마지막 요소에 붙어있던 감시카메라를 떼어내는 작업입니다.

        observer.current=new IntersectionObserver(entries=>{
            // 마지막 요소가 화면에 나타났고, 다음 페이지가 있다면
            if(entries[0].isIntersecting&&hasNext){
                setPage(prevPage=>prevPage+1);
            }
        });
        if(node) observer.current.observe(node);//관찰 고정시키는 역할
    },[loading,hasNext]);

    useEffect(()=>{
        const loadBoard=async()=>{
            setLoading(true);
            try{
                const url = `${BACKEND_API_BASE_URL}/board?page=${page}&size=10&sort=createdDate,desc`;
                const response=await fetchWithAccess(url);
                const data=await response.json();

                setBoards(prev => [...prev, ...data.content]);
            setHasNext(data.hasNext);
            }
            catch(err){
                console.error("데이터 로드 실패:", err);
            }
            finally{
                setLoading(false);
            }
        };
        loadBoard();
    },[page]);

    const handleSelectBoard=(board)=>{
        navigate(`/board/${board.boardId}`);
    }

    return(
        <div className="board-container">
            {boards.map((board,index)=>{
                if(boards.length==index+1){
                    return(
                        <article 
                            ref={lastBoardElementRef} 
                            key={board.boardId} 
                            className="review-post" 
                            onClick={()=>handleSelectBoard(board)} style={{cursor:"pointer"}}
                        >
                            <div className="post-header">
                                <img src={`https://image.tmdb.org/t/p/w200${board.tmdbPosterUrl}`} alt="포스터"/>
                                <h2>{board.title}<small>({board.tmdbTitle})</small></h2>
                            </div>

                            <div className="post-content">
                                <div
                                    className="content-body"
                                    dangerouslySetInnerHTML={{__html:board.content}}
                                />
                            </div>
                            <hr/>
                        </article>
                    );
                }
                else{
                    return(
                        <article key={board.boardId} className="review-post" onClick={()=>handleSelectBoard(board)} style={{cursor:"pointer"}}>
                        <div className="post-header">
                            <img src={`https://image.tmdb.org/t/p/w200${board.poster}`} alt="포스터"/>
                            <h2>{board.title}<small>({board.tmdbTitle})</small></h2>
                        </div>

                        <div className="post-content">
                            <div
                                className="content-body"
                                dangerouslySetInnerHTML={{__html:board.content}}
                            />
                        </div>
                        <hr/>
                    </article>
                    );
                }
            })}
            {loading&&<div className="loading">로딩 중...</div>}
        </div>
    );
}

export default BoardListPage;