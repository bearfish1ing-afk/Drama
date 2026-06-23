import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { fetchWithAccess } from "../util/fetchUtil";

const BACKEND_API_BASE_URL=import.meta.env.VITE_BACKEND_API_BASE_URL;

function DramaSearchPage(){
    const[keyword,setKeyword]=useState("");
    const[results,setResults]=useState([]);
    const[error,setError]=useState("");

    const navigate=useNavigate();

    const handleSearch=async (e)=>{
        e.preventDefault();
        setError("");

        try{
            const url=`${BACKEND_API_BASE_URL}/drama/search?keyword=${keyword}`;
            const res= await fetchWithAccess(url);
            const data=await res.json();
            setResults(data);//get이기에 body필요 없음 stringify필요X
        }catch (err) {
            console.error("검색 에러:", err);
            setError("검색 중 오류가 발생했습니다.");
        }
    }

    const handleSelectDrama=(drama)=>{
        navigate("/board/create",{
            state:{
                tmdbDramaId:drama.id,
                title:drama.name,
                poster:drama.posterPath,
                overview:drama.overview
            }//board에 가지고 갈것
        });
    }

    return(
        <div>
            <h1>드라마 검색</h1>
            <form onSubmit={handleSearch}>
                <input
                    type="text"
                    placeholder="드라마제목"
                    value={keyword}
                    onChange={e=>setKeyword(e.target.value)}
                />
                <button type="submit">검색</button>
            </form>

            {error && <p>{error}</p>}

            <ul>
                {results.map(drama=>(
                    <li key={drama.id} onClick={()=>handleSelectDrama(drama)} style={{cursor:"pointer"}}>
                        <img src={`https://image.tmdb.org/t/p/w200${drama.posterPath}`} alt={drama.name}/>
                        <p>{drama.name}</p>
                    </li>
                ))}
            </ul>
        </div>
    )
}

export default DramaSearchPage;