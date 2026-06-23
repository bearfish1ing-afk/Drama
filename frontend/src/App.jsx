import { BrowserRouter, Routes, Route, Navigate, Outlet } from "react-router-dom";

import BoardPage from "./pages/BoardPage";
import DramaSearchPage from "./pages/DramaSearchPage";
import BoardUpdatePage from "./pages/BoardUpdatePage";
import BoardListPage from "./pages/BoardListPage";
import BoardOnePage from "./pages/BoardOnePage";
import LoginPage from "./pages/LoginPage";
import JoinPage from "./pages/JoinPage";
import HomePage from "./pages/HomePage";
import MyPage from "./pages/MyPage";
import IdSearchPage from "./pages/IdSearchPage";
import FriendAllListPage from "./pages/FriendAllListPage";

import './App.css'

const ProtectedRoute = () => {
  const token = localStorage.getItem("accessToken");
  
  // 토큰이 없으면 로그인 페이지로 튕겨내고, 있으면 자식 컴포넌트(Outlet)를 보여줌
  return token ? <Outlet /> : <Navigate to="/login" replace />;
};

function App() {

  return (
    <BrowserRouter>
      <Routes>
        <Route path="/login" element={<LoginPage/>}/>
        <Route path="/signup" element={<JoinPage/>}/>
        <Route path="/home" element={<HomePage />} />

        {/* 로그인해야만 들어갈 수 있는 그룹 */}
        <Route element={<ProtectedRoute />}>
        <Route path="/mypage" element={<MyPage/>}/>
          <Route path="/board/create" element={<BoardPage />} />
          <Route path="/search" element={<DramaSearchPage />} />
          <Route path="/board/:boardId/update" element={<BoardUpdatePage />} />
          <Route path="/board" element={<BoardListPage />} />
          <Route path="/board/:boardId" element={<BoardOnePage />} />
          <Route path="/id/search" element={<IdSearchPage/>}/>
          <Route path="/friends" element={<FriendAllListPage/>}/>
        </Route>
      </Routes>
    </BrowserRouter>
  );
}

export default App
