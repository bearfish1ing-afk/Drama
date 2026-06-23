package com.example.demo.domain.friend.service;

import com.example.demo.domain.friend.dto.FriendRequestDTO;
import com.example.demo.domain.friend.dto.FriendResponseDTO;
import com.example.demo.domain.friend.entity.FriendStatus;
import com.example.demo.domain.friend.entity.FriendshipEntity;
import com.example.demo.domain.friend.repository.FriendShipRepository;
import com.example.demo.domain.jwt.service.JwtService;
import com.example.demo.domain.user.entity.UserEntity;
import com.example.demo.domain.user.repostiroy.UserRepository;
import com.example.demo.util.JWTUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FriendshipService {

    private final FriendShipRepository friendShipRepository;
    private final UserRepository userRepository;

    @Transactional
    //친구 요청보내기
    public String friendRequire(HttpServletRequest request,FriendRequestDTO dto){
        String token = request.getHeader("Authorization").replace("Bearer ", "");
        if(!JWTUtil.isValid(token,true)){
            throw new RuntimeException("유효하지 않은 토큰입니다.");
        }
        String sessionUsername = JWTUtil.getUsername(token);

        UserEntity requester=userRepository.findByUsername(sessionUsername)
                .orElseThrow(() -> new RuntimeException("로그인한 사용자 정보를 찾을 수 없습니다."));

        UserEntity receiver = userRepository.findById(Long.valueOf(dto.getReceiverId()))
                .orElseThrow(() -> new RuntimeException("상대방 사용자가 존재하지 않습니다."));

        if(requester.getId().equals(receiver.getId())){
            throw new RuntimeException("자기 자신에게는 친구 요청을 보낼 수 없습니다.");
        }

        FriendshipEntity entity=FriendshipEntity.builder()
                .receiver(receiver)
                .requester(requester)
                .status(FriendStatus.WAITING)
                .createdAt(LocalDateTime.now())
                .build();

        friendShipRepository.save(entity);

        return "친구 추가 요청 보내기 완료";

        //notificationService.send(receiver, requester.getNickname() + "님이 친구 요청을 보냈습니다.");
    }

    @Transactional
    //친구 받아주기
    public String friendAccept(Long friendshipId,String myUsername){
        FriendshipEntity existingRelation=friendShipRepository.findById(friendshipId)
                .orElseThrow(() -> new RuntimeException("요청이 없어요"));

        if(!existingRelation.getReceiver().getUsername().equals(myUsername)){
            throw new RuntimeException("본인에게 온 요청만 수락할 수 있습니다.");
        }

        existingRelation.acceptRoleChange();
        return "친구 추가가 되었습니다";
    }

    @Transactional
    //친구 차단
    public String friendReject(Long friendshipId,String myUsername){
        UserEntity currentUser = userRepository.findByUsername(myUsername)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        FriendshipEntity entity=friendShipRepository.findById(friendshipId)
                .orElseThrow(() -> new RuntimeException("요청이 없어요"));

        boolean isRelated=entity.getRequester().getId().equals(currentUser.getId())||
                entity.getReceiver().getId().equals(currentUser.getId());
        if(!isRelated){
            throw new RuntimeException("본인 요청으로만 차단할 수 있습니다.");
        }

        entity.rejectRoleChange();
        return "친구를 차단했습니다.";
    }

    @Transactional
    public List<FriendResponseDTO> getFriends(String myUsername){
        UserEntity me=userRepository.findByUsername(myUsername).orElseThrow();

        return friendShipRepository.findFollowFriends(me).stream()
                .map(entity->{
                    UserEntity friend=entity.getRequester().getUsername().equals(myUsername)
                            ?entity.getReceiver():entity.getRequester();

                    return new FriendResponseDTO(
                            entity.getId(),
                            friend.getNickname(),
                            entity.getStatus().name(),
                            entity.getCreatedAt()
                    );
                })
                .toList();
    }

    @Transactional
    public List<FriendResponseDTO> followFriend(String myUsername){
        UserEntity me=userRepository.findByUsername(myUsername).orElseThrow();

        return friendShipRepository.findAllFriend(me).stream()
                .map(entity->{
                    UserEntity friend=entity.getRequester().getUsername().equals(myUsername)
                            ?entity.getReceiver():entity.getRequester();

                    return new FriendResponseDTO(
                            entity.getId(),
                            friend.getNickname(),
                            entity.getStatus().name(),
                            entity.getCreatedAt()
                    );
                })
                .toList();
    }
}
