package moe.caa.multilogin.core.configuration.yggdrasil;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import moe.caa.multilogin.api.util.ValueUtil;
import moe.caa.multilogin.core.configuration.ConfException;
import moe.caa.multilogin.core.configuration.ProxyConfig;
import moe.caa.multilogin.core.configuration.SkinRestorerConfig;
import moe.caa.multilogin.core.configuration.yggdrasil.hasjoined.HasJoinedConfig;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@ToString
public class YggdrasilServiceConfig {
    private final int id;
    private final String name;

    private final HasJoinedConfig hasJoined;
    private final boolean passIp;
    private final int timeout;
    private final int retry;
    private final int retryDelay;
    private final ProxyConfig proxy;

    private final InitUUID initUUID;
    private final String nameAllowedRegular;
    private final boolean whitelist;
    private final boolean refuseRepeatedLogin;
    private final boolean compulsoryUsername;
    private final SkinRestorerConfig skinRestorer;

    public enum HttpRequestMethod {
        GET, POST
    }

    public enum InitUUID {
        DEFAULT, OFFLINE, RANDOM
    }

    public static YggdrasilServiceConfig read(CommentedConfigurationNode node) throws SerializationException, ConfException {
        int id = node.node("id").getInt();
        String name = node.node("name").getString("Unnamed");

        HasJoinedConfig hasJoined = HasJoinedConfig.getHasJoinedConfig(node.node("hasJoined"));

        boolean passIp = node.node("passIp").getBoolean(true);
        int timeout = node.node("timeout").getInt(10000);
        int retry = node.node("retry").getInt(0);
        int retryDelay = node.node("retryDelay").getInt(0);
        ProxyConfig proxy = ProxyConfig.read(node.node("proxy"));

        InitUUID initUUID = node.node("initUUID").get(InitUUID.class, InitUUID.DEFAULT);
        String nameAllowedRegular = node.node("nameAllowedRegular").getString("^[0-9a-zA-Z_]{3,16}$");
        boolean whitelist = node.node("whitelist").getBoolean(false);
        boolean refuseRepeatedLogin = node.node("refuseRepeatedLogin").getBoolean(false);
        boolean compulsoryUsername = node.node("compulsoryUsername").getBoolean(false);
        SkinRestorerConfig skinRestorer = SkinRestorerConfig.read(node.node("skinRestorer"));

        return checkValid(
                new YggdrasilServiceConfig(
                        id, name, hasJoined,
                        passIp, timeout, retry, retryDelay, proxy,
                        initUUID, nameAllowedRegular, whitelist,
                        refuseRepeatedLogin, compulsoryUsername, skinRestorer
                )
        );
    }

    private static YggdrasilServiceConfig checkValid(YggdrasilServiceConfig config) throws ConfException {
        if (config.id > 255 || config.id < 0)
            throw new ConfException(String.format(
                    "Yggdrasil id %d is out of bounds, The value can only be between 0 and 255."
                    , config.id
            ));
        if (config.passIp && ValueUtil.isEmpty(config.hasJoined.getIpContent()))
            throw new ConfException("PassIp is true, but ipContent is empty.");
        return config;
    }
}