package io.github.nguyennhatquang.fashion.Identity.usecase.strategyLogin;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import io.github.nguyennhatquang.fashion.Identity.usecase.IUseCase.ILoginStrategy;
import io.github.nguyennhatquang.fashion.common.Enum.TypeLoginEnum;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class LoginStrategyFactory {
    private final Map<TypeLoginEnum, ILoginStrategy> strategyMap;

    // Tự động Inject toàn bộ các class implement ILoginStrategy vào List
    public LoginStrategyFactory(List<ILoginStrategy> strategies) {
        strategyMap = new EnumMap<>(TypeLoginEnum.class);
        for (ILoginStrategy strategy : strategies) {
            strategyMap.put(strategy.getSupportedType(), strategy);
        }
    }

    public ILoginStrategy getStrategy(TypeLoginEnum loginType) {
        ILoginStrategy strategy = strategyMap.get(loginType);
        if (strategy == null) {
            throw new IllegalArgumentException("Không hỗ trợ phương thức đăng nhập: " + loginType);
        }
        return strategy;
    }
}
