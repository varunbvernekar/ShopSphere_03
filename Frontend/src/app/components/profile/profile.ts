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
  isLoading = false;
  message = '';

  constructor(
    private authService: AuthService,
    private userService: UserService
  ) { }

  ngOnInit(): void {
    this.loadProfile();
  }

  // Load User Profile
  loadProfile(): void {
    const currentUser = this.authService.getCurrentUser();

    if (!currentUser || !currentUser.id) {
      this.message = "User not logged in!";
      return;
    }

    this.isLoading = true;

    this.userService.getUser(currentUser.id).subscribe({
      next: (data) => {
        this.user = data;
        if (!this.user.address) {
          this.user.address = { street: '', city: '', state: '', zipCode: '', country: '' };
        }
        this.isLoading = false;
      },
      error: () => {
        this.message = "Failed to load profile!";
        this.isLoading = false;
      }
    });
  }

  //  Update Profile
  onSubmit(): void {
    if (!this.user) return;

    this.userService.updateUser(this.user).subscribe({
      next: (updatedUser) => {
        this.user = updatedUser;
        this.authService.updateCurrentUser(updatedUser);
        this.message = "Profile updated successfully!";
      },
      error: () => {
        this.message = "Update failed. Try again!";
      }
    });
  }
}
