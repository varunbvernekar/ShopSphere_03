import { Component } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { NgIf } from '@angular/common';

import { AuthService } from '../../../services/auth';
import { User, UserRole } from '../../../models/user';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [FormsModule, RouterLink, NgIf],
  templateUrl: './register.html',
  styleUrl: './register.css'
})
export class Register {
  name = '';
  email = '';
  phoneNumber = '';
  password = '';
  dateOfBirth = '';
  gender = '';

  errorMessage = '';

  constructor(
    private authService: AuthService,
    private router: Router
  ) { }

  onSubmit(): void {
    this.errorMessage = '';

    const passwordRegex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,}$/;
    if (!passwordRegex.test(this.password)) {
      this.errorMessage = 'Password must be at least 8 characters long and include an uppercase letter, a lowercase letter, a number, and a special character.';
      return;
    }

    const user: User = {
      name: this.name,
      email: this.email,
      phoneNumber: this.phoneNumber || '',
      password: this.password,
      address: {
        street: '',
        city: '',
        state: '',
        zipCode: '',
        country: ''
      },
      dateOfBirth: this.dateOfBirth ? this.dateOfBirth : undefined,
      gender: this.gender ? this.gender : undefined,
    };

    this.authService.register(user).subscribe({
      next: success => {
        if (success) {
          this.router.navigate(['/login'], {
            queryParams: { registered: 'true' }
          });
        } else {
          this.errorMessage = 'A user with this email already exists.';
        }
      },
      error: (err) => {
        if (err.error && err.error.message) {
          this.errorMessage = err.error.message;
        } else {
          this.errorMessage = 'Something went wrong during registration.';
        }
      }
    });
  }
}
