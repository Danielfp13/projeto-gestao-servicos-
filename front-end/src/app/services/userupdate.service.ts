import { Injectable } from '@angular/core';
import { Subject } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class UserUpdateService {
  private userUpdatedSubject = new Subject<void>();

  userUpdated$ = this.userUpdatedSubject.asObservable();

  notifyUserUpdated() {
    this.userUpdatedSubject.next();
  }
}
