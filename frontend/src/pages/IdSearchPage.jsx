import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { fetchWithAccess } from "../util/fetchUtil";

const BACKEND_API_BASE_URL=import.meta.env.VITE_BACKEND_API_BASE_URL;

function IdSearchPage(){
    const[keyword,setKeyword]=useState("");
    const[results,setResults]=useState([]);
    const[error,setError]=useState("");

    const navigate=useNavigate();

    const handleSearch=async (e)=>{
        e.preventDefault();
        setError("");

        try{
            const url=`${BACKEND_API_BASE_URL}/id/search?keyword=${keyword}`;
            const res= await fetchWithAccess(url);
            const data=await res.json();
            setResults(data);//get이기에 body필요 없음 stringify필요X
        }catch (err) {
            console.error("검색 에러:", err);
            setError("검색 중 오류가 발생했습니다.");
        }
    }

    const handleSelectId=async(receiverId)=>{
        try{
            const url = `${BACKEND_API_BASE_URL}/friends/request`;
            const res = await fetchWithAccess(url, {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ receiverId: receiverId }), // DTO 구조에 맞춤
            });

            if(res.ok){
                alert("친구 요청을 보냈습니다");
                setResults(prevResults => 
                prevResults.map(user => 
                    user.id === receiverId 
                        ? { ...user, friendshipStatus: "WAITING" } 
                        : user
                )
            );
            }
            else{
                const errorData = await res.text();
                alert(`요청 실패: ${errorData}`);
            }
        }
        catch (err) {
            console.error("친구 요청 에러:", err);
            setError("요청 중 오류가 발생했습니다.");
        }
    }

return (
        <div>
            <h1>사용자 아이디 검색</h1>
            <form onSubmit={handleSearch}>
                <input
                    type="text"
                    placeholder="아이디 검색"
                    value={keyword}
                    onChange={(e) => setKeyword(e.target.value)}
                />
                <button type="submit">검색</button>
            </form>

            {error && <p style={{ color: "red" }}>{error}</p>}

            {/* 검색 결과 리스트 출력 */}
            <div style={{ marginTop: "20px" }}>
                {results.length > 0 ? (
                    <ul>
                        {results.map((user) => (
                            <li key={user.id}>
                                <strong>{user.username}</strong> 
                                {user.friendshipStatus === "NONE" && (
                                <button 
                                    onClick={() => handleSelectId(user.id)}
                                    style={{ marginLeft: "10px" }}
                                >
                                    친구 요청 보내기
                                </button>
                                )}

                                {/* 대기 중일 때 */}
                                {user.friendshipStatus === "WAITING" && (
                                <span style={{ marginLeft: "10px", color: "orange" }}>요청 대기 중</span>
                                )}

                                {/* 이미 친구일 때 */}
                                {user.friendshipStatus === "ACCEPTED" && (
                                <span style={{ marginLeft: "10px", color: "green" }}>이미 친구입니다</span>
                                )}

                                {/* 차단 상태일 때 (필요시) */}
                                {user.friendshipStatus === "BLOCK" && (
                                <span style={{ marginLeft: "10px", color: "red" }}>차단됨</span>
                                )}
                            </li>
                        ))}
                    </ul>
                ) : (
                    keyword && <p>검색 결과가 없습니다.</p>
                )}
            </div>
        </div>
    );
}

export default IdSearchPage;