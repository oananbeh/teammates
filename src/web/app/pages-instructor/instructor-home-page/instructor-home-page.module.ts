import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterModule, Routes } from '@angular/router';
import { NgbCollapseModule, NgbDropdownModule, NgbTooltipModule } from '@ng-bootstrap/ng-bootstrap';
import { InstructorHomePageComponent } from './instructor-home-page.component';
import { CopyCourseModalModule } from '../../components/copy-course-modal/copy-course-modal.module';
import { LoadingRetryModule } from '../../components/loading-retry/loading-retry.module';
import { LoadingSpinnerModule } from '../../components/loading-spinner/loading-spinner.module';
import {
  ModifiedTimestampModalModule,
} from '../../components/modified-timestamps-modal/modified-timestamps-module.module';
import { PanelChevronModule } from '../../components/panel-chevron/panel-chevron.module';
import { ProgressBarModule } from '../../components/progress-bar/progress-bar.module';
import { SessionsTableModule } from '../../components/sessions-table/sessions-table.module';
import { TeammatesRouterModule } from '../../components/teammates-router/teammates-router.module';

const routes: Routes = [
  {
    path: '',
    component: InstructorHomePageComponent,
  },
];

/**
 * Module for instructor home page.
 */
@NgModule({
  declarations: [InstructorHomePageComponent],
  exports: [InstructorHomePageComponent],
  imports: [
    CommonModule,
    SessionsTableModule,
    FormsModule,
    RouterModule.forChild(routes),
    NgbCollapseModule,
    NgbDropdownModule,
    NgbTooltipModule,
    LoadingSpinnerModule,
    LoadingRetryModule,
    PanelChevronModule,
    TeammatesRouterModule,
    CopyCourseModalModule,
    ProgressBarModule,
    ModifiedTimestampModalModule,
  ],
})
export class InstructorHomePageModule { }
