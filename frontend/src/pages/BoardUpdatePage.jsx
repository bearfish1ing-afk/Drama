import { useParams, useNavigate } from "react-router-dom";
import { fetchWithAccess } from "../util/fetchUtil";
import ReactQuill from "react-quill";
import axios from "axios";
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

function BoardUpdatePage(){
    const quillRef=useRef();
    const { boardId } = useParams();
    const [tmdbDramaId, setTmdbDramaId] = useState(null);
    const [title, setTitle] = useState("");
    const [poster, setPoster] = useState("");
    const [overview, setOverview] = useState("");
    const [content, setContent] = useState(""); // 에디터 내용
    const [imageIds, setImageIds] = useState([]); // 업로드된 이미지 ID들
    const[error,setError]=useState("");

    useEffect(()=>{
        const getExistingData = async () => {
            try {
                const url = `${BACKEND_API_BASE_URL}/board/${boardId}`;
                const response = await fetchWithAccess(url);
                const data = await response.json();

                setTitle(data.tmdbTitle);
                setPoster(data.tmdbPosterUrl);
                setOverview(data.tmdbOverview);
                setContent(data.content);
                const existingIds = data.images.map(img => img.id);
                setImageIds(existingIds);
            } catch (err) {
                console.error("데이터 로딩 실패", err);
                setError("기존 데이터를 불러오는 데 실패했습니다.");
            }
        };

        if(boardId) getExistingData();
    },[boardId]);

    const handleWrite=async(e)=>{
        e.preventDefault();//새로고침 막기
        setError("");//이전 발생 에러메시지 초기화

        try{
            const url=`${BACKEND_API_BASE_URL}/board/${boardId}/update`;
            const res = await fetchWithAccess(url, {
                method: "PUT",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ content,imageIds })
            });

            if(!res.ok) throw new Error("드라마 기록 실패");
            alert("수정이 완료되었습니다!");
            navigate(`/board/${boardId}`);
        }catch(err){
            setError("드라마 기록 중 오류가 발생했습니다.");
        }
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
                body: formData // FormData 전송 시 Content-Type 헤더는 비워둡니다.
            });
            const data = await res.json();

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

export default BoardUpdatePage;