import { useState, useMemo, useRef, useEffect, useCallback } from "react";
import { useNavigate } from "react-router-dom";
import { fetchWithAccess } from "../util/fetchUtil";
import axios from "axios";
import "react-quill/dist/quill.snow.css";

const BACKEND_API_BASE_URL = import.meta.env.VITE_BACKEND_API_BASE_URL;

function FriendAllListPage(){
    const [friends, setFriends] = useState([]);
    const [loading, setLoading] = useState(true);

    const AllFriends=async()=>{
        try {
            const url = `${BACKEND_API_BASE_URL}/friends/list`;
            const res = await fetchWithAccess(url);
            if (res.ok) {
                const data = await res.json();
                setFriends(data);
            }
        } catch (err) {
            console.error("친구 목록 로딩 에러:", err);
        } finally {
            setLoading(false);
        }
    }

    useEffect(()=>{
        AllFriends();
    },[]);

    const handleAccept=async(friendshipId)=>{
        try {
            const url = `${BACKEND_API_BASE_URL}/friends/accept/${friendshipId}`;
            const res = await fetchWithAccess(url, { method: "POST" });
            if (res.ok) {
                alert("친구 수락 완료!");
                AllFriends(); // 목록 새로고침
            }
        } catch (err) {
            alert("수락 중 오류 발생");
        }
    }

    const handleReject=async(friendshipId)=>{
        try{
            const url = `${BACKEND_API_BASE_URL}/friends/reject/${friendshipId}`;
            const res=await fetchWithAccess(url,{method:"POST"});
            if(res.ok){
                alert("처리 완료");
                AllFriends();
            }
        }catch(err){
            alert("처리 중 오류 발생");
        }
    }

    return (
        <div>
            <h1>친구 관리 목록</h1>
            <table>
                <thead>
                    <tr>
                        <th>닉네임</th>
                        <th>상태</th>
                        <th>작업</th>
                    </tr>
                </thead>
                <tbody>
                    {friends.map((f) => (
                        <tr key={f.friendshipId}>
                            <td>{f.friendNickname}</td>
                            <td>{f.status}</td>
                            <td>
                                {f.status === "WAITING" && (
                                    <>
                                        <button onClick={() => handleAccept(f.friendshipId)}>수락</button>
                                        <button onClick={() => handleReject(f.friendshipId)}>거절</button>
                                    </>
                                )}
                                {f.status === "ACCEPTED" && <span>친구</span>}
                                {f.status === "REJECTED" && <span>차단됨</span>}
                            </td>
                        </tr>
                    ))}
                </tbody>
            </table>
        </div>
    );
}

export default FriendAllListPage;