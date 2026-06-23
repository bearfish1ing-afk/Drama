package com.example.demo.api;

import com.example.demo.domain.friend.dto.FriendRequestDTO;
import com.example.demo.domain.friend.dto.FriendResponseDTO;
import com.example.demo.domain.friend.service.FriendshipService;
import com.example.demo.domain.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/friends")
public class FriendController {
    private final FriendshipService friendshipService;

    public FriendController(FriendshipService friendshipService) {
        this.friendshipService = friendshipService;
    }
    //친구 요청 보내기
    @PostMapping("/request")
    public ResponseEntity<String>sendRequestApi(HttpServletRequest username, @RequestBody FriendRequestDTO dto){
        String response=friendshipService.friendRequire(username,dto);
        return ResponseEntity.ok(response);
    }
    //친구 요청 수락하기
    @PostMapping("/accept/{friendshipId}")
    public ResponseEntity<String> acceptFriendship(@PathVariable("friendshipId") Long friendshipId,@AuthenticationPrincipal String username){
        String response=friendshipService.friendAccept(friendshipId,username);
        return ResponseEntity.ok(response);
    }
    //친구 요청 거절
    @PostMapping("/reject/{friendshipId}")
    public ResponseEntity<String> rejectFriend(
            @PathVariable Long friendshipId,
            @AuthenticationPrincipal String username) {

        String result = friendshipService.friendReject(friendshipId, username);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/follow")
    public ResponseEntity<List<FriendResponseDTO>> getFollowApi(@AuthenticationPrincipal String username) {
        List<FriendResponseDTO> friends = friendshipService.getFriends(username);
        return ResponseEntity.ok(friends);
    }

    @GetMapping("/list")
    public ResponseEntity<List<FriendResponseDTO>> getAllApi(@AuthenticationPrincipal String username) {
        List<FriendResponseDTO> follows = friendshipService.followFriend(username);
        return ResponseEntity.ok(follows);
    }
}
