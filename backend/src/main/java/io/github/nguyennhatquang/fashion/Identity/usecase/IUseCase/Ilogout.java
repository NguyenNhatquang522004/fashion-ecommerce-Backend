package io.github.nguyennhatquang.fashion.Identity.usecase.IUseCase;

import io.github.nguyennhatquang.fashion.common.response.Result;

public interface Ilogout {
    Result<Void, Exception> Logout(String refreshToken);
}
