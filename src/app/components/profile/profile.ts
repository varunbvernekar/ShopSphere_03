import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { User, Address } from '../../models/user';
import { AuthService } from '../../services/auth';
import { UserService } from '../../services/user';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './profile.html',
  styleUrls: ['./profile.css']
})
export class Profile implements OnInit {
  user: User | null = null;
  originalUser: User | null = null;
  isLoading = false;
  isSaving = false;
  errorMessage = '';
  successMessage = '';

  constructor(
    private authService: AuthService,
    private userService: UserService
  ) { }

  ngOnInit(): void {
    this.loadProfile();
  }

  loadProfile(): void {
    const currentUser = this.authService.getCurrentUser();

    if (!currentUser || !currentUser.id) {
      return;
    }

    this.isLoading = true;
    this.errorMessage = '';
    this.successMessage = '';

    this.userService.getUser(currentUser.id).subscribe({
      next: user => {
        const completeUser: User = {
          ...user,
          phoneNumber: user.phoneNumber || '',
          dateOfBirth: user.dateOfBirth || '',
          gender: user.gender || '',
          address: user.address || {
            street: '',
            city: '',
            state: '',
            zipCode: '',
            country: ''
          }
        };

        this.user = completeUser;
        this.originalUser = JSON.parse(JSON.stringify(completeUser));
        this.isLoading = false;
      },
      error: err => {
        if (err.status === 0) {
          this.errorMessage = 'Unable to connect to the server. Please ensure the backend is running.';
          this.isLoading = false;
          return;
        }
        if (err.status === 404) {
          const currentUser = this.authService.getCurrentUser();
          if (currentUser) {
            const completeUser: User = {
              ...currentUser,
              phoneNumber: currentUser.phoneNumber || '',
              dateOfBirth: currentUser.dateOfBirth || '',
              gender: currentUser.gender || ''
            };
            this.user = completeUser;
            this.originalUser = JSON.parse(JSON.stringify(completeUser));
            this.isLoading = false;
            return;
          }
        }
        this.errorMessage = 'Failed to load profile. Please try again.';
        this.isLoading = false;
      }
    });
  }

  onSubmit(): void {
    if (!this.user || !this.user.id) {
      this.errorMessage = 'User data is invalid.';
      return;
    }

    this.isSaving = true;
    this.errorMessage = '';
    this.successMessage = '';

    const userToSave: User = {
      ...this.user,
      phoneNumber: this.user.phoneNumber || '',
      dateOfBirth: this.user.dateOfBirth || '',
      gender: this.user.gender || ''
    };

    this.userService.updateUser(userToSave).subscribe({
      next: updatedUser => {
        const completeUser: User = {
          ...updatedUser,
          phoneNumber: updatedUser.phoneNumber || '',
          dateOfBirth: updatedUser.dateOfBirth || '',
          gender: updatedUser.gender || '',
          address: updatedUser.address || {
            street: '',
            city: '',
            state: '',
            zipCode: '',
            country: ''
          }
        };

        this.authService.updateCurrentUser(completeUser);
        this.user = completeUser;
        this.originalUser = JSON.parse(JSON.stringify(completeUser));
        this.successMessage = 'Profile updated successfully!';
        this.isSaving = false;

        setTimeout(() => {
          this.successMessage = '';
        }, 3000);
      },
      error: err => {
        console.error('Failed to update profile', err);
        if (err.status === 0) {
          this.errorMessage = 'Unable to connect to the server. Please ensure the backend is running.';
        } else {
          this.errorMessage = 'Failed to update profile. Please try again.';
        }
        this.isSaving = false;
      }
    });
  }

  resetForm(): void {
    if (this.originalUser) {
      this.user = JSON.parse(JSON.stringify(this.originalUser));
      this.errorMessage = '';
      this.successMessage = '';
    }
  }
}
