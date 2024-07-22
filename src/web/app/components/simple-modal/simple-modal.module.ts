import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { SimpleModalComponent } from './simple-modal.component';
import { ProgressBarModule } from '../progress-bar/progress-bar.module';

/**
 * Module for the modal component
 */
@NgModule({
  declarations: [SimpleModalComponent],
  exports: [SimpleModalComponent],
  imports: [
    CommonModule,
    ProgressBarModule,
    NgbModule,
  ],
})

export class SimpleModalModule { }
