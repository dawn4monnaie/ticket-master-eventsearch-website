/// <reference types="@types/googlemaps" />
import { Component, OnInit, ViewChild, Injectable, Renderer2, NgZone, ChangeDetectorRef } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { NgForm } from '@angular/forms';
import { AgmCoreModule, MapsAPILoader } from '@agm/core';

import { FormControl } from '@angular/forms';

import { map, debounceTime } from "rxjs/operators";

import { trigger, state, style, animate, transition } from '@angular/animations';



@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css'],
  animations: [
    trigger('fadeIn', [
      transition('true => *', [
        style({opacity: 0}),
        animate(1000)
      ]),
      // fade out when destroyed. this could also be written as transition('void => *')
      transition('false => *',
        animate(1000, style({opacity: 0})))
    ]),
    trigger('leftIn', [
      state('in', style({transform: 'translateX(0)'})),
      transition('* => in', [
        style({transform: 'translateX(-100%)'}),
        animate('500ms ease-in')
      ])
    ]),
    trigger('rightIn', [
      state('in', style({transform: 'translateX(0)'})),
      transition('* => in', [
        style({transform: 'translateX(+100%)'}),
        animate('500ms ease-in')
      ])
    ])
  ]
})
export class AppComponent implements OnInit {

  myControl = new FormControl();
  options: string[] = ['One', 'Two', 'Three'];

  currentLocation;
  currentLocationStatus = false;
  locationValue;
  directionsService;
  directionsDisplay;

  map;
  error;

  keywordStatus;
  locationStatus;
  searchButtonStatus;
  locationValidStatus;

  from = 'current';
  category = 'all';
  lengthUnits = 'miles';
  
  searchTerm:FormControl;
  searchResult = [];

  seatmapDisplay='none'; 

  openModalDialog(){
    this.seatmapDisplay='block'; //Set block css
    return false;
  }

  closeModalDialog(){
    this.seatmapDisplay='none'; //set none css after close dialog
    return false;
  }


  @ViewChild("keyword") keyword;
  @ViewChild("mapsTab") mapsTab;
  @ViewChild("location") location;

  @ViewChild("fromlocation") fromlocation;
  @ViewChild("directionsPanel") directionsPanel;
  @ViewChild("reviewsDropdown") reviewsDropdown;
  @ViewChild("orderDropdown") orderDropdown;

  constructor(private http: HttpClient, private renderer: Renderer2, 
    private mapsAPILoader: MapsAPILoader, private ngZone: NgZone, 
    private changeDetector : ChangeDetectorRef) {
      this.searchTerm = new FormControl();
  }

  searchTermValue;

  ngOnInit () {
    /*
    this.currentLocationStatus = true;
    this.currentLocation = {
      lat: 34.0223519,
      lng: -118.285117
    };
    */

    this.getCurrentLocation();
    // localStorage.clear();
    

    this.searchTerm.valueChanges.pipe(debounceTime(400)).subscribe(
      data => {
        this.search_word(data).subscribe(
          response =>{
            this.searchResult = response;
          },
          error =>{
            //this.error = error;
            //console.log(this.error);
          }
        );
        this.searchTermValue = this.keyword.nativeElement.value
        // console.log(this.searchTermValue),
        // console.log(data);
        this.onKeyword(data);
        this.blurKeyword(data);

      },
      error => {
        //console.log(error);
      }
    )
  }

  checkSearchTerm(){
    if (this.keyword.nativeElement.value == ""){
      this.renderer.addClass(this.keyword.nativeElement, "is-invalid");
      this.keywordStatus = false;
      this.checkSearchButtonStatus();
    }
  }



  onClear(searchForm : NgForm) {
    //searchForm.reset();

    this.keywordStatus = false;
    this.searchButtonStatus = false;
    this.locationStatus = false;
    this.locationValidStatus = false;

    this.progrossBar = false;

    this.from = 'current';
    this.category = 'all';
    this.locationValue = '';
    this.eventIdDisplay = "";
    this.eventIdDisplay = "";
    
    this.eventsDisplay = [];

    this.favorites = [];
    this.favoritesDisplay = [];
    this.favoritesDisplayIndex = 0;

    this.resultTable = false;
    this.favoriteTable = false;
    this.detailsTab = false;

    this.renderer.removeClass(this.keyword.nativeElement, "is-invalid");
    this.renderer.removeClass(this.location.nativeElement, "is-invalid");

    this.keyword.nativeElement.value = '';
    this.location.nativeElement.value = '';

    this.noRecords = false;
    this.failedSearch = false;

    this.placesIn = "";
    this.detailsIn = "";

    this.distance = "";
    this.lengthUnits = 'miles';

  }

  
  search_word(term:string){
    return this.http.get(this.ticketmasterKeywordURL + 'keyword=' + term).pipe(map((res: any) => {
      //console.log('res', res);
      return res['_embedded']['attractions'];
    }));
  }


  // listen ng-touched
  blurKeyword(value) {
    setTimeout(() => {
      var touched = this.keyword.nativeElement.classList.contains('ng-touched');
      if (value == '' && touched) {
        this.renderer.addClass(this.keyword.nativeElement, "is-invalid");
        this.keywordStatus = false;
        this.checkSearchButtonStatus();
      }
    }, 10);
  }

  onKeyword(value) {
    if (!this.checkValidation(value)) {
      this.renderer.addClass(this.keyword.nativeElement, "is-invalid");
      this.keywordStatus = false;
      this.checkSearchButtonStatus();
    }
    else {
      this.renderer.removeClass(this.keyword.nativeElement, "is-invalid");
      this.keywordStatus = true;
      this.checkSearchButtonStatus();
    }
  }

  disableLocation() {
    this.locationStatus = false;
    this.renderer.removeClass(this.location.nativeElement, "is-invalid");
    this.location.nativeElement.value = '';
    this.checkSearchButtonStatus();
  }

  enableLocation() {
    this.locationStatus = true;
    this.checkSearchButtonStatus();
  }

  onLocation(value) {
    if (!this.checkValidation(value)) {
      this.renderer.addClass(this.location.nativeElement, "is-invalid");
      this.locationValidStatus = false;
      this.checkSearchButtonStatus();
    }
    else {
      this.renderer.removeClass(this.location.nativeElement, "is-invalid");
      this.locationValidStatus = true;
      this.checkSearchButtonStatus();
    }
  }

  blurLocation(value) {
    setTimeout(() => {
      var touched = this.location.nativeElement.classList.contains('ng-touched');
      if (value == '' && touched) {
        this.renderer.addClass(this.location.nativeElement, "is-invalid");
        this.locationValidStatus = false;
        this.checkSearchButtonStatus();
      }
    }, 10);
  }

  detailsTab = false;
  favoriteTable = false;

  eventsDisplay;
  searchLocation;
  noRecords = false;
  resultTable = false;
  failedSearch = false;
  progrossBar = false;

  placesIn = "";


  eventIdDisplay = ''
  detailsObject;
  detailsObjectName;
  detailsObjectEventId;
  // mapTabSelected=false;
  detailsIn = "";

  event_artistteamsname="";
  event_venuesname="";
  event_date_localDate="";
  event_date_localTime="";
  event_category="";
  event_prices="";
  event_status="";
  event_url="";
  event_seatmap="";

  artistteamsArray;


  artistMetaInfo;

  photosMetaInfo;

  artistFlag=false;

  venueMetaInfo;
  venueMetaInfoReversed;

  venueMetaInfo_part;


  distance;

  // resultTable && !noRecords && !failedSearch && !detailsTab
  async requestPlaces (searchForm : NgForm) {
    this.noRecords = false;
    this.failedSearch = false;
    this.detailsTab = false;
    // this.mapTabSelected = false;
    this.favoriteTable = false;
    this.resultTable = false;
    this.progrossBar = true;
    this.searchLocation = this.currentLocation;

    if (searchForm.value["from"] == "other") {
      var locationUrl = this.serverURL + "location?keyword="+ searchForm.value['location'];
      const response = await this.http.get(locationUrl).toPromise();
      // console.log(response);
      let locationEle = response['results']['0']['geometry']['location']
      this.searchLocation = {
        lat: locationEle['lat'],
        lng: locationEle['lng']
      };
      
    }

    this.distance = searchForm.value["distance"] == undefined ? 10 : searchForm.value["distance"];
    if (searchForm.value['lengthUnits'] == "kilometers"){
      this.distance = Math.round(this.distance * 0.6);
    }

    var eventsUrl = this.serverURL + 'events?'
      + 'lat=' + this.searchLocation.lat
      + '&lng=' + this.searchLocation.lng
      + '&category=' + searchForm.value["category"]
      + '&keyword=' + this.searchTermValue.split(' ').join('+')
      + '&distance=' + this.distance;
      //+ '&unit=' + searchForm.value['lengthUnits'];
    
    // console.log(eventsUrl);
    this.http.get(eventsUrl).subscribe(
      data => {  
        this.eventsDisplay = [];
        // console.log(data);
        // let i = 0;
        if (data['page']['totalElements'] == 0){
          // console.log("No Element Found");
          this.failedSearch = true;
          this.progrossBar = false;
          this.placesIn = "";

        } else{
          data['_embedded']['events'].forEach(element => {   
            this.eventsDisplay.push({
                // index: ++i,
                date: element['dates']['start']['localDate'],
                event: element['name'],
                category: element['classifications']['0']['genre']['name'] + "-" + element['classifications']['0']['segment']['name'],
                venueInfo: element['_embedded']['venues']['0']['name'],
                event_id: element['id']
              }
            )
          });
          this.eventsDisplay.sort(function(a, b){ 
            return new Date(a.date) > new Date(b.date) ? 1:-1;
          });
          this.resultTable = true;
          if (this.eventsDisplay.length == 0) {
            this.noRecords = true;
          }
          else {
            this.noRecords = false;
          }
          this.placesIn = "";
          this.progrossBar = false;
        }
        
        // console.log(this.eventsDisplay);
      },
      error => {
        this.error = error;
        console.log(this.error);
        this.resultTable = true;
        this.favoriteTable = false;
        this.detailsTab = false;
        this.noRecords = false;
        

        this.failedSearch = true;
        // this.mapTabSelected = false;
        this.placesIn = "";
        this.progrossBar = false;
      }
    )
  }

  onBack2List() {
    this.detailsIn = "";
    this.placesIn = "in";
    this.noRecords = false;
    this.detailsTab = false;
    // this.mapTabSelected = false;
  }

  twitterUrl;
  detailsEventIdObject;

  UpcomingEventsExist=true;

  onDetails(event_id) {
    this.detailsTab = true;
    this.detailsIn = 'in';
    this.placesIn = "";
    // this.changeDetector.detectChanges();
    this.eventIdDisplay="";
    this.progrossBar = true;
    this.detailsEventIdObject = {};

    let event_details_url = this.serverURL + "eventdetails?event_id=" + event_id;
    this.http.get(event_details_url).subscribe(
      data => {
        // console.log(data);
        this.detailsObject = data;
        this.detailsObjectName = data['name'];
        this.detailsObjectEventId = data['id'];
        this.eventIdDisplay = data['id'];
        // Check out EVENT located at VENUE. Website: URL #CSCI571EventSearch
        this.twitterUrl = "https://twitter.com/intent/tweet?text="
          + "Check out " + this.detailsObject['name']
          + " located at " + this.detailsObject['_embedded']['venues']['0']['name']
          + ". Website:&url=" + this.detailsObject['url']
          + "&hashtags=CSCI571EventSearch";

        let obj = this.detailsObject;

        this.artistteamsArray = [];
        this.event_artistteamsname = "";
        if ('_embedded' in obj && 'attractions' in obj['_embedded']){
          obj['_embedded']['attractions'].forEach(element => 
            this.artistteamsArray.push(element['name'])
          );
          if (this.artistteamsArray.length != 0){
            this.event_artistteamsname = this.artistteamsArray.join(" | ")
          }
        }
    
        let venuesnameArray = [];
        this.event_venuesname = "";
        if ('_embedded' in obj && 'venues' in obj['_embedded']){
          obj['_embedded']['venues'].forEach(element => 
            venuesnameArray.push(element['name'])
          );
          if (venuesnameArray.length != 0){
            this.event_venuesname = venuesnameArray.join(" | ")
          }
        }
    
        this.event_date_localDate = "";
        this.event_date_localTime = "";
        if ('dates' in obj && 'start' in obj['dates']){
          let temp_date=obj['dates']['start'];
    
          if ('localDate' in temp_date){
            this.event_date_localDate = temp_date['localDate']
          }
          if ('localTime' in temp_date){
            this.event_date_localTime = temp_date['localTime']
          }
        }
    
        this.event_category="";
        this.artistFlag = false;
        if ('classifications' in obj && '0' in obj['classifications']){
          let temp_catergory=obj['classifications']['0'];
          if ('genre' in temp_catergory){
            this.event_category += temp_catergory['genre']['name']
          }
          if ('segment' in temp_catergory){
            this.event_category += " | " + temp_catergory['segment']['name']
            if (temp_catergory['segment']['name'] == "Music"){       
              this.artistFlag = true;
            }
            
          }
        }
    
        this.event_prices="";
        if ('priceRanges' in obj && '0' in obj['priceRanges']){
          let prices = obj['priceRanges'][0];
          let currency = "$";
          if ('currency' in prices){ 
            currency = prices['currency'];
          }

          let temp_flag = false;
          if ('min' in prices){
            this.event_prices = currency + prices['min']
            temp_flag = true;
          }
          if (temp_flag)
          this.event_prices += " ~ "
          if ('max' in prices){ 
            this.event_prices += currency + prices['max'];
          }
          
        }
    
        this.event_status = "";
        if ('dates' in obj && 'status' in obj['dates'] && 'code' in obj['dates']['status']){
          this.event_status=obj['dates']['status']['code'];
        }
        this.event_url = "";
        if ('url' in obj){
          this.event_url=obj['url'];
        }
        this.event_seatmap = "";
        if ('seatmap' in obj && 'staticUrl' in obj['seatmap']){
          this.event_seatmap=obj['seatmap']['staticUrl'];
        }

        this.detailsEventIdObject['date'] = this.event_date_localDate;
        this.detailsEventIdObject['event'] = this.detailsObjectName;
        this.detailsEventIdObject['category'] = this.event_category;
        this.detailsEventIdObject['venueInfo'] = this.event_venuesname;
        this.detailsEventIdObject['event_id'] = this.detailsObjectEventId;

        // console.log(this.artistteamsArray);

        this.artistMetaInfo = {};
        this.photosMetaInfo = {};


        this.artistteamsArray.forEach(artistteams => {
          this.artistMetaInfo[artistteams] = {};
          if (this.artistFlag) {
              let artist_details_url = this.serverURL + "artistdetails?keyword=" + artistteams;
              this.http.get(artist_details_url).subscribe(
              data_artist => {
                  //console.log(data_artist);
                  let artist_info = data_artist['artists']['items']
                  var BreakException = {};
                  let metaInfo = {};
                  try {
                    artist_info.forEach( artist_info_meta => {
                      if (artist_info_meta['name'] == artistteams){
                        metaInfo['name'] = artist_info_meta['name'];
                        metaInfo['followers'] = artist_info_meta['followers']['total'];
                        metaInfo['popularity'] = artist_info_meta['popularity'];
                        metaInfo['url'] = artist_info_meta['external_urls']['spotify'];
                        throw BreakException;
                      }
                    });
                  }catch (e) {
                    if (e !== BreakException) { console.error(e); throw e; };
                  }
                  if (metaInfo != {}){
                    this.artistMetaInfo[artistteams] = metaInfo;
                    //this.artistMetaInfo.push(metaInfo);
                  }
                  //console.log(this.artistMetaInfo);
                }
              )
            }

            let photos_url = this.serverURL + "photos?keyword=" + artistteams.split(" ").join('+');
            // console.log(photos_url);
            this.photosMetaInfo[artistteams] = {"0":[], "1":[], "2":[]};
                
            this.http.get(photos_url).subscribe(
              photo_data => {
                // console.log(photo_data);
                
                if (photo_data['items']) {
                  let i = 0;
                  let height0=0, height1=0, height2=0;

                  //console.log(photo_data['items']);
                  photo_data['items'].forEach(photo => {
                    if (height0 <= Math.min(height1, height2)){
                      i = 0;
                      height0 += photo['image']['height'] / photo['image']['width']
                    }
                    else if (height1 <= Math.min(height0, height2)){
                      i = 1;
                      height1 += photo['image']['height'] / photo['image']['width']
                    }
                    else{
                      i = 2;
                      height2 += photo['image']['height'] / photo['image']['width']
                    }

                    if (i == 0) {
                      this.photosMetaInfo[artistteams]["0"].push(photo['link']);
                    }
                    else if (i == 1) {
                      this.photosMetaInfo[artistteams]["1"].push(photo['link']);
                    }
                    else{
                      this.photosMetaInfo[artistteams]["2"].push(photo['link']);
                    }
                    
                });
              }
              // console.log(this.photosMetaInfo[artistteams]);
            });

        });


        this.venueMetaInfo = [];
        let venuename = obj['_embedded']['venues']['0']['name'];
        let venue_url = this.serverURL + "venues?venuename=" + venuename;
        this.http.get(venue_url).subscribe(
          venue_data => {
            // console.log(venue_data);
            let query_id = venue_data['resultsPage']['results']['venue']['0']['id'];
            let venuedetail_url = this.serverURL + "venuesdetail?id=" + query_id;
            this.http.get(venuedetail_url).subscribe(
              venuedetail_data => {
                // console.log(venuedetail_data);
                let events_tmp = venuedetail_data['resultsPage']['results'];
                this.UpcomingEventsExist=true;

                if ('event' in events_tmp){
                  let events = events_tmp['event'];

                  events.forEach(element => {
                    let event_dic = {};
                    event_dic['displayName'] = element['displayName'];
                    event_dic['uri'] = element['uri'];
                    if (element['performance'].length != 0) {
                      event_dic['artist'] = element['performance']['0']['displayName'];
                    } else{
                      event_dic['artist'] = "";
                    }
                    event_dic['date'] = element['start']['date'];
                    event_dic['time'] = element['start']['time'];
                    // event_dic['date'] = element['start']['date'] + " " + element['start']['time'];;
                    event_dic['type'] = element['type'];
                    this.venueMetaInfo.push(event_dic);
                  });

                  this.venueMetaInfoReversed = this.venueMetaInfo.slice().reverse(); 
                  this.venueMetaInfo_part = this.venueMetaInfo.slice(0, 5);

                } else {
                  this.UpcomingEventsExist=false;
                }

              });
          }
        );
        this.progrossBar = false;
      }
    )
    return false;
  }



  venue_event;
  marker;
  venue_event_name="";
  venue_event_address="";
  venue_event_location="";
  venue_event_phoneNumber="";
  venue_event_openHours="";
  venue_event_generalRule="";
  venue_event_childlRule="";

  onSelectMap() {

    // this.changeDetector.detectChanges();
    // when the map tab display : block, refresh the map

    this.venue_event = this.detailsObject['_embedded']['venues']['0'];
    
    this.venue_event_name="";
    this.venue_event_address="";
    this.venue_event_location="";
    this.venue_event_phoneNumber="";
    this.venue_event_openHours="";
    this.venue_event_generalRule="";
    this.venue_event_childlRule="";
    // console.log(this.venue_event);
    if ('name' in this.venue_event){
      this.venue_event_name=this.venue_event['name'];
    }
    if ('address' in this.venue_event && 'line1' in this.venue_event['address']){
      this.venue_event_address=this.venue_event['address']['line1'];
    }
    if ('city' in this.venue_event){
      this.venue_event_location=this.venue_event['city']['name']
    }
    if ('state' in this.venue_event){
      if ('city' in this.venue_event){
        this.venue_event_location += ", ";
      }
      this.venue_event_location += this.venue_event['state']['name'];
    }
    if ('boxOfficeInfo' in this.venue_event){
      if ('phoneNumberDetail' in this.venue_event['boxOfficeInfo']){
      this.venue_event_phoneNumber=this.venue_event['boxOfficeInfo']['phoneNumberDetail'];
      }
      if ('openHoursDetail' in this.venue_event['boxOfficeInfo']){
        this.venue_event_openHours=this.venue_event['boxOfficeInfo']['openHoursDetail'];
      }
    }
    if ('generalInfo' in this.venue_event){
      if ('generalRule' in this.venue_event['generalInfo']){
        this.venue_event_generalRule=this.venue_event['generalInfo']['generalRule'];
      }
      if ('childRule' in this.venue_event['generalInfo']){
        this.venue_event_childlRule=this.venue_event['generalInfo']['childRule'];
      }
    }
    

    // console.log(this.venue_event);
    let venue_location = {
      lat: parseFloat(this.venue_event['location']['latitude']),
      lng: parseFloat(this.venue_event['location']['longitude'])
    };

    this.mapsAPILoader.load().then(() => {
      this.map = new google.maps.Map(this.mapsTab.nativeElement, {
        center: venue_location,
        zoom: 15
      });
      this.directionsService = new google.maps.DirectionsService();
      this.directionsDisplay = new google.maps.DirectionsRenderer();
      google.maps.event.trigger(this.map, 'resize');
      // clear map first
      this.directionsDisplay.setMap(null);
      this.directionsDisplay.setPanel(null);

      this.map.setCenter(venue_location);
      if (this.marker) {
        this.marker.setMap(null);
      }
      this.marker = new google.maps.Marker({
        position: venue_location
      });
      this.marker.setMap(this.map);
    });

  }

  venuesorttype="default";
  venueorder="ascending";


  onSearchVenue(){
    // empty, nothing need to be done here.  

  }



  onVenueSort(): void {  // event will give you full breif of action
    //console.log(this.venuesorttype);
    //console.log(this.venueorder);


    if (this.venuesorttype == "default" || this.venuesorttype == "time"){
      
      if (this.venueorder == "ascending"){
        if (!this.showmoreorlessFlag){
          this.venueMetaInfo_part = this.venueMetaInfo;
        }else{
          this.venueMetaInfo_part = this.venueMetaInfo.slice(0, 5);
        }
      }else{
        if (!this.showmoreorlessFlag){
          this.venueMetaInfo_part = this.venueMetaInfoReversed;
        }else{
          this.venueMetaInfo_part = this.venueMetaInfoReversed.slice(0, 5);
        }
      }
    } else { 
      if (this.venuesorttype == "eventname"){
        this.venueMetaInfo_part.sort(this.onVenueSortByEventname);
      } else if (this.venuesorttype == "artist"){
        this.venueMetaInfo_part.sort(this.onVenueSortByArtist);
      } else if (this.venuesorttype == "type"){
        this.venueMetaInfo_part.sort(this.onVenueSortByType);
      } else{
        //console.error("Type does not exist")
      }
      if (this.venueorder != "ascending"){
        this.venueMetaInfo_part.reverse();
      }
    }
    this.changeDetector.detectChanges();
  }

  onVenueSortByEventname(a, b){
    return a.displayName.toLowerCase() > b.displayName.toLowerCase() ? 1 : -1;
  }

  onVenueSortByArtist(a, b){
    return a.artist.toLowerCase() > b.artist.toLowerCase() ? 1 : -1;
  }

  onVenueSortByType(a, b){
    return a.type.toLowerCase() > b.type.toLowerCase() ? 1 : -1;
  }

  
  showmoreorlessFlag=true;
  showmoreorless(){
    this.showmoreorlessFlag = !this.showmoreorlessFlag
    if (!this.showmoreorlessFlag){
      this.venueMetaInfo_part = this.venueMetaInfo;
    }else{
      this.venueMetaInfo_part = this.venueMetaInfo.slice(0, 5)
    }
    this.onVenueSort();

  }


  stroke: number = 2;
  radius: number = 20;
  semicircle: boolean = false;
  rounded: boolean = false;
  responsive: boolean = false;
  clockwise: boolean = false;
  color: string = '#32cd32';
  background: string = '#eaeaea';
  duration: number = 800;
  animation: string = 'easeOutCubic';
  animationDelay: number = 0;
  animations: string[] = [];
  gradient: boolean = false;
  realCurrent: number = 0;
  getOverlayStyle() {
    let isSemi = this.semicircle;
    let transform = (isSemi ? '' : 'translateY(80%) ') + 'translateX(50%)';
    return {
      'top': 'auto',
      'bottom': 'auto',
      'left': 'auto',
      'transform': transform,
      '-moz-transform': transform,
      '-webkit-transform': transform,
      'font-size': this.radius / 1.2 + 'px'
    };
  }


  objectKeys(obj) {
    if(obj)
      return Object.keys(obj);
  }

  onEventInfo() {
    // this.mapTabSelected = false;
    this.noRecords = false;
  }

  onArtiestTeamInfo(){

  }

  onResults() {
    this.resultTable = true;
    this.favoriteTable = false;
    this.detailsTab = false;
    // this.mapTabSelected = false;
    this.placesIn = "";
    if (this.eventsDisplay.length == 0) {
      this.noRecords = true;
    }
    else {
      this.noRecords = false;
    }
    this.failedSearch = false;
  }


  onFavorites() {
    this.resultTable = false;
    this.favoriteTable = true;
    this.detailsTab = false;
    // this.mapTabSelected = false;
    this.placesIn = "";
    if (localStorage.favorite) {
      // it needs update only if the array favorites is empty.
      if (this.favorites.length == 0) {
        this.favoritesDisplayIndex = 0;
        this.updateFavorites();
      }
      this.noRecords = false;
    }
    else {
      //alert
      this.noRecords = true;
    }
    this.failedSearch = false;
  }




//"{"index":1,"date":"2018-12-05","event":"6LACK w/ Tierra Whack","category":"Other-Music",
// "venueInfo":"The Novo by Microsoft","event_id":"Z7r9jZ1Aeo4AS"}"
  onFavoriteStar(id, obj) {
    let content = [];
    //console.log(obj);
    if (localStorage.favorite) {
      let contentJSON = localStorage.favorite;
      content = JSON.parse(contentJSON);
    }
    content.push(id);
    localStorage.favorite = JSON.stringify(content);
    localStorage[id] = JSON.stringify(obj);
    //console.log(localStorage);
    this.updateFavorites();
  }

  deleteFavoriteStar(id) {
    let content = [];
    if (localStorage.favorite) {
      var contentJSON = localStorage.favorite;
      content = JSON.parse(contentJSON);
    }
    let index = content.indexOf(id);
    content.splice(index, 1);
    if (content.length == 0) {
      localStorage.removeItem("favorite");
    }
    else {
      localStorage.favorite = JSON.stringify(content);
    }
    localStorage.removeItem(id);
    // console.log(localStorage);
    this.updateFavorites();
  }


  favoritesDisplayIndex = 0;
  favorites = [];
  favoritesDisplay = [];

  updateFavorites() {
    if (localStorage.favorite) {      
      this.favoritesDisplay = [];
      this.favorites = [];
      var favoritesJSON = JSON.parse(localStorage.favorite);
      //console.log(favoritesJSON);
      //console.log(localStorage.favorite);
      let num = 0;
      favoritesJSON.forEach(id => {
        var element = JSON.parse(localStorage[id]);
        num += 1
        this.favoritesDisplay.push({
              index: num,
              date: element['date'],
              event: element['event'],
              category: element['category'],
              venueInfo: element['venueInfo'],
              event_id: element['event_id'] 
          }
        );
      });      
      this.noRecords = false;
    }
    else {
      // alert only if on favorite page
      if (this.favoriteTable) {
        this.noRecords = true;
      }
    }
  }






  checkExists(event_id) {
    
    return false || localStorage[event_id];
  }

// ============================ 

  private ipApiUrl:string = 'http://ip-api.com/json';
  private ticketmasterkey = "uy2mh8pBbjq91kGF2qse3pSUWbw4oZAS";
  private ticketmasterKeywordURL = 'https://app.ticketmaster.com/discovery/v2/suggest?apikey='+this.ticketmasterkey+'&';
  private serverURL = "https://csci-571-hw8-dawn.appspot.com/";


  private getCurrentLocation () {
    this.http.get(this.ipApiUrl).subscribe(
      data => {
        this.currentLocation = {
          lat: data['lat'],
          lng: data['lon']
        };
        this.currentLocationStatus = true;
      },
      error =>  {
        this.error = error;
        this.currentLocationStatus = false;
      }
    );
  }

  private checkSearchButtonStatus() {
    if (!this.locationStatus) {
      this.searchButtonStatus = this.currentLocationStatus && this.keywordStatus;
    }
    else {
      this.searchButtonStatus = this.currentLocationStatus && this.keywordStatus && this.locationValidStatus;
    }
  }

  private checkValidation(value) {
    if (value == null) {
      return true;
    }
    for (var i = 0; i < value.length; ++i) {
      if (value[i] != ' ') {
        return true;
      }
    }
    return false;
  }

}

interface latlng{
  lat:number;
  lng:number;
}

