package server.data.DTOs;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DHRequestTO {
    private String ID;
    private Long p;
    private Long g;
    private Long A;
}
