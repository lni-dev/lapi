package me.linusdev.discordbotapi.api.objects.user.abstracts;

import me.linusdev.discordbotapi.api.communication.cdn.image.CDNImage;
import me.linusdev.discordbotapi.api.communication.cdn.image.CDNImageRetriever;
import me.linusdev.discordbotapi.api.communication.cdn.image.ImageQuery;
import me.linusdev.discordbotapi.api.communication.file.types.AbstractFileType;
import me.linusdev.discordbotapi.api.lapiandqueue.LApi;
import me.linusdev.discordbotapi.api.objects.HasLApi;
import me.linusdev.discordbotapi.api.objects.snowflake.Snowflake;
import me.linusdev.discordbotapi.api.objects.snowflake.SnowflakeAble;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface BasicUserInformation extends SnowflakeAble, HasLApi {

    /**
     * the username (in front of the '#')
     */
    @NotNull
    String getUsername();

    /**
     * the Discriminator (the numbers after the '#')
     */
    @NotNull
    String getDiscriminator();

    /**
     * snowflake id of the user
     * It's easier to use {@link #getId()}
     */
    @NotNull
    Snowflake getIdAsSnowflake();

    /**
     * the id of the user as string
     */
    default @NotNull String getId(){
        return getIdAsSnowflake().asString();
    }

    /**
     * the avatar of the user as hash
     */
    @Nullable
    String getAvatarHash();

    /**
     *
     * @param desiredSize the desired file size, a power of 2 between {@value ImageQuery#SIZE_QUERY_PARAM_MIN} and {@value ImageQuery#SIZE_QUERY_PARAM_MAX}
     * @param fileType see {@link CDNImage#ofUserAvatar(LApi, String, String, AbstractFileType) restrictions} and {@link me.linusdev.discordbotapi.api.communication.file.types.FileType FileType}
     * @return {@link me.linusdev.discordbotapi.api.lapiandqueue.Queueable Queueable} to retrieve the avatar
     */
    default CDNImageRetriever getAvatar(int desiredSize, @NotNull AbstractFileType fileType){
        if(getAvatarHash() == null) throw new IllegalArgumentException("This user object has no avatar hash");
        return new CDNImageRetriever(CDNImage.ofUserAvatar(getLApi(), getId(), getAvatarHash(), fileType), desiredSize, true);
    }

    /**
     *
     * @param fileType see {@link CDNImage#ofUserAvatar(LApi, String, String, AbstractFileType) restrictions} and {@link me.linusdev.discordbotapi.api.communication.file.types.FileType FileType}
     * @return {@link me.linusdev.discordbotapi.api.lapiandqueue.Queueable Queueable} to retrieve the avatar
     */
    default CDNImageRetriever getAvatar(@NotNull AbstractFileType fileType){
        if(getAvatarHash() == null) throw new IllegalArgumentException("This user object has no avatar hash");
        return new CDNImageRetriever(CDNImage.ofUserAvatar(getLApi(), getId(), getAvatarHash(), fileType));
    }

}
