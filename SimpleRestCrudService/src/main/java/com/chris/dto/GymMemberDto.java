package com.chris.dto;

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

    private Long id;
    private String firstName;
    private String lastName;
    private String email;

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

            return mem.firstName.equals(this.firstName) &&
                    mem.lastName.equals(this.lastName) &&
                    mem.email.equals(this.email);
        }

        return false;
    }

    @Override
    public int hashCode() {
        String base = String.format("%s:%s:%s", firstName, lastName, email);
        return base.hashCode();
    }
}




























