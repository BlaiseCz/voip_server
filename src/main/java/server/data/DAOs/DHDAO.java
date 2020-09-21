package server.data.DAOs;

import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import server.data.DTOs.DHRequestTO;

@Data
@Document(collection = "DH")
@AllArgsConstructor
@Builder
@Getter
@Setter
public class DHDAO {
    @Id
    private ObjectId _id;
    private Long p;
    private Long g;
    private Long _s;
    private Long S;
    @Builder.Default
    private Long secret = null;
}
