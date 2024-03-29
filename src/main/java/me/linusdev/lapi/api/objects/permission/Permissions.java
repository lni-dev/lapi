/*
 * Copyright (c) 2022 Linus Andera
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package me.linusdev.lapi.api.objects.permission;

import me.linusdev.data.SimpleDatable;
import me.linusdev.lapi.api.interfaces.copyable.Copyable;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Objects;

/**
 * This class represents a set of {@link Permission permissions}.
 * @see Permission
 */
public class Permissions implements SimpleDatable, Copyable<Permissions> {

    private @NotNull BigInteger setBits;

    public Permissions(){
        this.setBits = new BigInteger(new byte[]{0});
    }

    public Permissions(@NotNull BigInteger setBits){
        this.setBits = setBits;
    }

    @Contract("_ -> new")
    public static @NotNull Permissions ofString(@Nullable String setBits){
        if(setBits == null) return new Permissions();
        return new Permissions(new BigInteger(setBits));
    }

    public void set(String setBits){
        this.setBits = new BigInteger(setBits);
    }

    /**
     * Adds given permissions. If it is already contained, nothing will happen
     * @param permission {@link Permission} to add
     */
    public void addPermission(@NotNull Permission permission){
        setBits = setBits.setBit(permission.getSetBitIndex());
    }

    /**
     * Removes given permissions. If it is not contained, nothing will happen
     * @param permission {@link Permission} to remove
     */
    public void removePermission(@NotNull Permission permission){
        setBits = setBits.clearBit(permission.getSetBitIndex());
    }

    /**
     * The returned int is backed by this {@link Permissions} object, but may be dropped at any time!
     * @return {@link BigInteger} with set bits
     */
    @ApiStatus.Internal
    public @NotNull BigInteger getInt() {
        return setBits;
    }

    /**
     *
     * @return decimal value as string
     */
    public @NotNull String getValueAsString(){
        return setBits.toString();
    }

    /**
     * the returned List will not change if this {@link Permissions} object is changed.
     * @return List of {@link Permission}
     */
    @NotNull
    @Contract(value = "-> new", pure = true)
    public ArrayList<Permission> toPermissionList(){
        return Permission.getPermissionsFromBits(getInt());
    }

    /**
     *
     * @param permission the {@link Permission} to check
     * @return true if given {@link Permission} is set, false otherwise. If given permission is {@code null}, false will be returned
     */
    public boolean isSet(@Nullable Permission permission){
        if(permission == null) return false;
        return setBits.testBit(permission.getSetBitIndex());
    }

    @Override
    public String toString() {
        return toPermissionList().toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Permissions that = (Permissions) o;
        return setBits.equals(that.setBits);
    }

    @Override
    public int hashCode() {
        return Objects.hash(setBits);
    }

    @Override
    public Object simplify() {
        return getValueAsString();
    }

    @NotNull
    @Override
    public Permissions copy() {
        return new Permissions(new BigInteger(setBits.toByteArray()));
    }
}
