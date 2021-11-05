import { InputLabel } from '@mui/material';
import { Component } from 'react';
import BasicMenu from "../Menu/BasicMenu";
import VerifyAccessPanel from "../VerifyAccessPanel/VerifyAccessPanel";
import VerifyTrustedClientCertificatePanel from '../VerifyTrustedClientCertificatePanel/VerifyTrustedClientCertificatePanel';
import ManageCertificatesPanel from '../ManageCertificatesPanel/ManageCertificatesPanel';

export default class Dashboard extends Component {
    constructor(props) {
        super(props);

        this.state ={
            specificPanel: ''
        };

        this.changePanel = this.changePanel.bind(this);
    }

    changePanel(specificPanel) {
        this.setState({
            specificPanel: specificPanel
        });
    }

    render() {
        let panel = (<VerifyAccessPanel></VerifyAccessPanel>);

        if(this.state.specificPanel && this.state.specificPanel == "Verify the service is trusted") {
            panel = (<VerifyTrustedClientCertificatePanel></VerifyTrustedClientCertificatePanel>);
        }
        else if (this.state.specificPanel && this.state.specificPanel == "Manage certificates") {
            panel = (<ManageCertificatesPanel></ManageCertificatesPanel>);
        }

        return (
            <div>
                <BasicMenu changePanel={this.changePanel}></BasicMenu>

                {panel}
            </div>
        );
    }
}

