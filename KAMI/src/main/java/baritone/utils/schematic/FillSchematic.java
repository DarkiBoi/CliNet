/*
 * This file is part of Baritone.
 *
 * Baritone is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Baritone is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Baritone.  If not, see <https://www.gnu.org/licenses/>.
 */

package baritone.utils.schematic;

import baritone.api.schematic.ISchematic;
import net.minecraft.block.state.IBlockState;

import java.util.List;

public class FillSchematic implements ISchematic {

    private final int widthX;
    private final int heightY;
    private final int lengthZ;
    private final IBlockState state;

    public FillSchematic(int widthX, int heightY, int lengthZ, IBlockState state) {
        this.widthX = widthX;
        this.heightY = heightY;
        this.lengthZ = lengthZ;
        this.state = state;
    }

    @Override
    public IBlockState desiredState(int x, int y, int z, IBlockState current, List<IBlockState> approxPlaceable) {
        return state;
    }

    @Override
    public int widthX() {
        return widthX;
    }

    @Override
    public int heightY() {
        return heightY;
    }

    @Override
    public int lengthZ() {
        return lengthZ;
    }
}
