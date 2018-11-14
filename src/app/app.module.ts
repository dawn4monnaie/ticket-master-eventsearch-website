import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { HttpClientModule } from '@angular/common/http';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { ProgressbarModule } from 'ngx-bootstrap';
import { TabsModule } from 'ngx-bootstrap';
import { ModalModule } from 'ngx-bootstrap';
import { BsDropdownModule } from 'ngx-bootstrap';
import { AgmCoreModule } from '@agm/core';

import { AppComponent } from './app.component';
import 'hammerjs';
import {RoundProgressModule} from 'angular-svg-round-progressbar';


import {DragDropModule} from '@angular/cdk/drag-drop';
import {ScrollingModule} from '@angular/cdk/scrolling';
import {CdkTableModule} from '@angular/cdk/table';
import {CdkTreeModule} from '@angular/cdk/tree';
import {
  MatFormFieldModule,
  MatAutocompleteModule,
  MatBadgeModule,
  MatBottomSheetModule,
  MatButtonModule,
  MatButtonToggleModule,
  MatCardModule,
  MatCheckboxModule,
  MatChipsModule,
  MatDatepickerModule,
  MatDialogModule,
  MatDividerModule,
  MatExpansionModule,
  MatGridListModule,
  MatIconModule,
  MatInputModule,
  MatListModule,
  MatMenuModule,
  MatNativeDateModule,
  MatPaginatorModule,
  MatProgressBarModule,
  MatProgressSpinnerModule,
  MatRadioModule,
  MatRippleModule,
  MatSelectModule,
  MatSidenavModule,
  MatSliderModule,
  MatSlideToggleModule,
  MatSnackBarModule,
  MatSortModule,
  MatStepperModule,
  MatTableModule,
  MatTabsModule,
  MatToolbarModule,
  MatTreeModule,
  MatTooltipModule,
} from '@angular/material';



@NgModule({
  declarations: [
    AppComponent
  ],
  imports: [
    BrowserModule,

    BrowserAnimationsModule,
    HttpClientModule,

    FormsModule,
    ReactiveFormsModule, 
    MatTooltipModule,

    MatInputModule,
    MatAutocompleteModule,
    MatFormFieldModule,

    RoundProgressModule,
    
    ProgressbarModule.forRoot(),
    TabsModule.forRoot(),
    ModalModule.forRoot(),
    BsDropdownModule.forRoot(),
    AgmCoreModule.forRoot({
      apiKey: 'AIzaSyB-UV6eMbgrMrfaZ4x4m0w1-stENc6fO-8',
      libraries: ['places']
    })
  ],

  exports: [
    CdkTableModule,
    CdkTreeModule,
    DragDropModule,
    MatAutocompleteModule,
    MatBadgeModule,
    MatBottomSheetModule,
    MatButtonModule,
    MatButtonToggleModule,
    MatCardModule,
    MatCheckboxModule,
    MatChipsModule,
    MatStepperModule,
    MatDatepickerModule,
    MatDialogModule,
    MatDividerModule,
    MatExpansionModule,
    MatGridListModule,
    MatIconModule,
    MatInputModule,
    MatListModule,
    MatMenuModule,
    MatNativeDateModule,
    MatPaginatorModule,
    MatProgressBarModule,
    MatProgressSpinnerModule,
    MatRadioModule,
    MatRippleModule,
    MatSelectModule,
    MatSidenavModule,
    MatSliderModule,
    MatSlideToggleModule,
    MatSnackBarModule,
    MatSortModule,
    MatTableModule,
    MatTabsModule,
    MatToolbarModule,
    MatTooltipModule,
    MatTreeModule,
    ScrollingModule,
  ],

  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
