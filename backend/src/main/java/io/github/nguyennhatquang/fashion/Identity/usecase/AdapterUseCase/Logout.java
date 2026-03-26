package io.github.nguyennhatquang.fashion.Identity.usecase.AdapterUseCase;

import org.springframework.stereotype.Service;

import io.github.nguyennhatquang.fashion.Identity.usecase.IUseCase.Ilogout;
import io.github.nguyennhatquang.fashion.common.response.Result;
import io.github.nguyennhatquang.fashion.common.shared.IKeycloak;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class Logout implements Ilogout {
    private final IKeycloak keycloakRepo;

    @Override
    public Result<Void, Exception> Logout(String refreshToken) {
        try {
            keycloakRepo.logout(refreshToken);
            return Result.success(null);
        } catch (Exception e) {
            return Result.error(e);
        }
    }

}
