package com.example.demo;


import java.time.LocalDateTime;

import java.util.HashSet;
import java.util.Set;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "users")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "user_id")
	private Long id;

	@NotNull
	@Column(length = 50, nullable = false, unique = true)
	@Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")

	private String userName;

	@Column(nullable = false)
	@NotBlank(message = "Password is required")
	private String password;

	private String firstName;

	private String lastName;

	@Email(message = "Email should be valid")
	private String emailId;

	@Pattern(regexp = "^[0-9]{10}$", message = "Mobile number must be 10 digits")
	private String mobileNumber;

	private String address;

	@ElementCollection(fetch = FetchType.EAGER)
	@Enumerated(EnumType.STRING)
	@Column(name = "role")
	private Set<Role> roles;
	
	private int failedLoginAttempts = 0;

	private LocalDateTime lastLoginAt;

	private LocalDateTime passwordChangedAt;
	private boolean isEmailVerified = false;
	private boolean isMobileVerified = false;
	private boolean accountLocked = false;
	private String createdBy;
	private String updatedBy;

	private LocalDateTime deletedAt;

	@Enumerated(EnumType.STRING)
	private UserStatus userStatus;
	@CreationTimestamp
	private LocalDateTime createdAt;
	@UpdateTimestamp
	private LocalDateTime updatedAt;

	@PrePersist
	protected void onCreate() {
		if (this.userStatus == null) {
			this.userStatus = UserStatus.ACTIVE;
		}
		this.createdAt = LocalDateTime.now();
		this.updatedAt = LocalDateTime.now();
	}

	@Override
	public String toString() {
		return "User [id=" + id + ", userName=" + userName + ", password=" + password + ", firstName=" + firstName
				+ ", lastName=" + lastName + ", emailId=" + emailId + ", mobileNumber=" + mobileNumber + ", address="
				+ address + ", roles=" + roles + ", failedLoginAttempts=" + failedLoginAttempts + ", lastLoginAt="
				+ lastLoginAt + ", passwordChangedAt=" + passwordChangedAt + ", isEmailVerified=" + isEmailVerified
				+ ", isMobileVerified=" + isMobileVerified + ", accountLocked=" + accountLocked + ", createdBy="
				+ createdBy + ", updatedBy=" + updatedBy + ", deletedAt=" + deletedAt + ", userStatus=" + userStatus
				+ ", createdAt=" + createdAt + ", updatedAt=" + updatedAt + "]";
	}

}
