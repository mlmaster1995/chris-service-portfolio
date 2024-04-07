/**
 * MIT License
 *
 * Copyright (c) 2024 Chris Yang
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.chris.dto;

import com.chris.entity.GymMemberEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.Comparator;

/**
 * member dto transferred between client and server
 */
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class GymMemberDto implements Serializable, Comparator {
    private Integer id;
    private String firstName;
    private String lastName;
    private String email;

    public GymMemberDto(String firstName, String lastName, String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    public GymMemberEntity toEntity() {
        return new GymMemberEntity(this.id, this.firstName, this.lastName, this.email);
    }

    @Override
    public int compare(Object o1, Object o2) {
        if (o1 instanceof GymMemberDto && o2 instanceof GymMemberDto) {
            GymMemberDto mem1 = (GymMemberDto) o1;
            GymMemberDto mem2 = (GymMemberDto) o2;

            if (!mem1.firstName.equals(mem2.firstName)) {
                return mem1.firstName.compareTo(mem2.firstName);
            } else if (!mem1.lastName.equals(mem2.lastName)) {
                return mem1.lastName.compareTo(mem2.lastName);
            } else {
                return mem1.email.compareTo(mem2.email);
            }
        }
        return 0;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof GymMemberDto) {
            GymMemberDto mem = (GymMemberDto) obj;

            return mem.firstName.equals(this.firstName) && mem.lastName.equals(this.lastName) && mem.email.equals(this.email);
        }

        return false;
    }

    @Override
    public int hashCode() {
        String base = String.format("%s:%s:%s", firstName, lastName, email);
        return base.hashCode();
    }
}



















