package me.settingdust.deathpenalty;

import com.google.inject.Inject;
import io.izzel.amber.commons.i18n.AmberLocale;
import java.util.Objects;
import org.spongepowered.api.plugin.Plugin;

@Plugin(
    id = Constants.ID,
    name = "DeathPenalty",
    version = "2.0",
    description = "Death with penalty",
    authors = { "SettingDust" }
)
public class DeathPenalty {

    @Inject
    public DeathPenalty(DeathPenaltyModulesService deathPenaltyModulesService, AmberLocale locale) {
        Objects.requireNonNull(locale);
        Objects.requireNonNull(deathPenaltyModulesService);
    }
}
