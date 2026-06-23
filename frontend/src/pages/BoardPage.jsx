import { useState, useMemo, useRef } from "react";
import { useLocation } from "react-router-dom";
import { fetchWithAccess } from "../util/fetchUtil";
import ReactQuill from "react-quill";
import { useNavigate } from "react-router-dom";
import "react-quill/dist/quill.snow.css";

const BACKEND_API_BASE_URL = import.meta.env.VITE_BACKEND_API_BASE_URL;

export const CustomToolbar = () => (
    <div id="toolbar">
        <span className="ql-formats">
            <select className="ql-size" defaultValue="medium">
                <option value="small">Small</option>
                <option value="medium">Medium</option>
                <option value="large">Large</option>
                <option value="huge">Huge</option>
            </select>
        </span>
        <span className="ql-formats">
            <button className="ql-bold" />
            <button className="ql-italic" />
            <button className="ql-underline" />
        </span>
        <span className="ql-formats">
            <select className="ql-color" />
            <select className="ql-background" />
        </span>
        <span className="ql-formats">
            <button className="ql-image" />
        </span>
        <span className="ql-formats">
            <button className="ql-clean" />
        </span>
    </div>
);

function BoardPage(){
    const location=useLocation();//현재페이지에 대한 정보 가져오기
    const{tmdbDramaId,title,poster,overview}=location.state||{};
    /*React Router에서 페이지 이동 시 추가 데이터를 전달할 수 있음
    예를 들어, 검색 페이지 → BoardPage 이동 시 선택한 드라마 정보 전달
    */
    const quillRef=useRef();
    const navigate = useNavigate();
    const[content,setContent]=useState("");
    const [imageIds, setImageIds] = useState([]);
    const[error,setError]=useState("");

    const handleWrite=async(e)=>{
        e.preventDefault();//새로고침 막기
        setError("");//이전 발생 에러메시지 초기화


        try{
            const url=`${BACKEND_API_BASE_URL}/board`;

            const res = await fetchWithAccess(url, {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ tmdbDramaId, content, imageIds })
            });

            alert("기록이 완료되었습니다!");
            navigate("/board");
        }catch{
            setError("드라마 기록 중 오류가 발생했습니다.");
        }
        navigate("/home");
    };

    const handleImageUpload=async(file)=>{
        if(!file){
            return;
        }

        const formData=new FormData();//실제 파일을 담는 가상의 봉투
        formData.append("file",file);

        try{
            const url = `${BACKEND_API_BASE_URL}/images`;
            const res = await fetchWithAccess(url, {
                method: "POST",
                body: formData, 
            });
            const data=await res.json();

            if(data.id){
                setImageIds(prev=>[...prev,data.id]);
                /*
                prev는 현재까지 저장된 이미지 목록
                ...prev는 목록을 펼쳐놓은것
                data.id는 새로 받은 id추가
                */
            }

            const editor=quillRef.current.getEditor();
            const range=editor.getSelection()||{index:editor.getLength()};
            editor.insertEmbed(range.index,"image",data.imageUrl);
            editor.setSelection(range.index + 1);
            //range.index는 커서위치
        }
        catch(err){
            console.error("Upload error:", err);
            alert("이미지 업로드에 실패했습니다.");
        }
    }

    const modules=useMemo(()=>{
        return{
            toolbar:{
                container:"#toolbar",
                handlers:{//toolbar에 대한 동작
                    image:()=>{
                        const input=document.createElement('input');//가상의 파일 선택창
                        input.setAttribute('type','file');//파일 선택버튼 생성
                        input.setAttribute('accept','image/*');//.jpg,.png만 보이도록
                        input.onchange = () => {//열기를 누르는 순간의 이벤트
                            const file = input.files[0];//고른 여러개의 사진이 있으면 첫번째것을
                            handleImageUpload(file);
                        };
                        input.click();//컴퓨터의 파일 탐색기 열러준다
                    },
                },
            },
        };
    },[]);

    //alt는 대체 텍스트 이미지가 보이지 않을 경우 대신 표시되는 글자
    return(
        <div>
            <h1>{title}</h1>
            <img src={`https://image.tmdb.org/t/p/w300${poster}`} alt={title} />
            <p>{overview}</p>
            <form onSubmit={handleWrite}>
                <CustomToolbar/>
                <ReactQuill
                    ref={quillRef}
                    theme="snow"
                    value={content}
                    onChange={setContent} // Quill은 내부적으로 보정하므로 setContent만 연결해도 됨
                    modules={modules}
                    placeholder="드라마 감상 기록을 작성하세요..."
                />
                {error && <p>{error}</p>}

                <button type="submit">작성완료</button>
            </form>
        </div>
    );
}

/*
    handleImageUpload가 onSubmit에 없는이유
    1. "미리 올리기" 방식 (현재 코드의 방식)
    사용자가 에디터에서 이미지 버튼을 누르고 파일을 선택하면:

    즉시 백엔드(/images)로 이미지를 보냅니다.

    백엔드는 이미지를 저장하고 **"이미지가 저장된 인터넷 주소(URL)"**를 돌려줍니다.

    에디터에는 실제 파일이 아니라 그 URL만 <img src="http://..."> 태그로 삽입됩니다.

    나중에 [작성완료] 버튼을 누를 때(handleWrite), 서버에는 **이미지 파일이 아닌 URL이 포함된 텍스트(HTML)**만 전송됩니다.

    장점: handleWrite 시점에 전송하는 데이터 양이 매우 작아서 업로드가 순식간에 끝납니다.
*/
export default BoardPage;