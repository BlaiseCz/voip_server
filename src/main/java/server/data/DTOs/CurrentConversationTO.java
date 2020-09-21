package server.data.DTOs;

import lombok.*;
import server.data.DAOs.ConversationDAO;
import server.data.DAOs.UserShortDAO;

import java.util.HashSet;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@Setter
public class CurrentConversationTO {
    private String conversationID;
    private Long began;
    private Boolean isOngoing;
    private Set<UserShortDAO> participants;
    private Set<String> currentParticipants;

    public static CurrentConversationTO map(ConversationDAO conversationDAO) {
        return CurrentConversationTO.builder()
                .conversationID(conversationDAO.get_id().toString())
                .currentParticipants((conversationDAO.getCurrentParticipants() != null)
                        ? conversationDAO.getCurrentParticipants().keySet()
                        : new HashSet<>())
                .began(conversationDAO.getBegan())
                .isOngoing(conversationDAO.getIsOngoing())
                .participants(conversationDAO.getParticipants())
                .build();
    }
}
