export enum  NotifyType {
  Success,
  Info,
  Warning,
  Error
}

export interface NotifyEvent {
  message: string,
  title: string
  type: NotifyType,
}
