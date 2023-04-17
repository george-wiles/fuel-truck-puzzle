import { Injectable } from '@angular/core';
import { Observable, Subject } from 'rxjs';
import { NotifyEvent, NotifyType } from './model/notify.model';

@Injectable({
  providedIn: 'root',
})
export class NotifyService {
  toastEvents: Observable<NotifyEvent>;
  private _toastEvents = new Subject<NotifyEvent>();

  constructor() {
    this.toastEvents = this._toastEvents.asObservable();
  }

  showErrorNotify(title: string, message: string) {
    this._toastEvents.next({
      message,
      title,
      type: NotifyType.Error,
    });
  }
}
