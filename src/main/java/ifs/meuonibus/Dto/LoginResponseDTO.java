package ifs.meuonibus.Dto;

import lombok.Builder;

@Builder

public record LoginResponseDTO(String token,String refreshToken) {


}
