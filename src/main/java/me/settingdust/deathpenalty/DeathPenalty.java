package me.settingdust.deathpenalty;

import com.google.inject.Inject;
import io.izzel.amber.commons.i18n.AmberLocale;
import java.util.Objects;
import org.spongepowered.api.plugin.Plugin;

@Plugin(
    id = Constants.ID,
    name = "RPG Death",
    version = "2.1",
    description = "Death with penalty & spawn fake player after log out",
    authors = { "SettingDust" }
)
public class DeathPenalty {

    @Inject
    public DeathPenalty(DeathPenaltyModulesService deathPenaltyModulesService, AmberLocale locale) {
        Objects.requireNonNull(locale);
        Objects.requireNonNull(deathPenaltyModulesService);
    }
}
