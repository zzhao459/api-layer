import React, {  useMemo } from "react";
import Styles from "./Style";
import { styled } from '@mui/material/styles';
import { useForm, useField } from "react-final-form-hooks";
import Button from '@mui/material/Button';
import Stack from '@mui/material/Stack';
import './VerifyAccessPanel.css'
function VerifyAccessPanel() {
    const Input = styled('input')({
        display: 'none',
    });
    const sleep = ms => new Promise(resolve => setTimeout(resolve, ms));
    const [serviceUrl, setServiceUrl] = React.useState(null);
    const onSubmit = async values => {
        await sleep(300);

        fetch(values.url, {
            method: 'POST',
            mode: 'cors'
        });
        // window.alert(JSON.stringify(values, 0, 2));
    };

    const onVerify = async () => {
        await sleep(300);
        // TODO use the below commented URL to route through gateway
        // const url = process.env.REACT_APP_GATEWAY_URL + process.env.REACT_APP_CERTIFICATE_SERVICE_HOME + `/verify?url=${serviceUrl}`

        const url = `certificate-service/verify?url=${serviceUrl}`
        fetch(url, {
            method: 'GET',
            mode: 'cors'
        }).catch(() => {
            alert("failed to fetch")
        });
    };

    const validate = values => {
        const errors = {};

        setServiceUrl(values.url)
        if (!values.url) {
            errors.url = "Required";
        }
        if (!values.lastName) {
            errors.lastName = "Required";
        }
        return errors;
    };

    const initialValues = useMemo(
            () => ({
                url: "",
            }),
            []
    );

        const {form, handleSubmit, pristine, submitting} = useForm({
            onSubmit,
            initialValues,
            validate
        });

        const url = useField("url", form);

        return (
            <Styles>
                <h2> Verify access panel</h2>
                <form onSubmit={handleSubmit(onSubmit)}>
                    <div>
                        <label>Hostname</label>
                        <input {...url.input} placeholder="Service Hostname"/>
                        {url.meta.touched && url.meta.error && (
                            <span>{url.meta.error}</span>
                        )}
                    </div>

                    <div className="buttons">
                        <Stack direction="row" alignItems="center" spacing={2}>
                            <label htmlFor="contained-button-file">
                                <Button  style={{textTransform: 'none'}} onClick={onVerify} variant="contained" component="span">
                                    Verify Service
                                </Button>
                            </label>
                        </Stack>
                        <button type="submit" disabled={submitting}>
                            Add certificate
                        </button>
                        <button
                            type="button"
                            onClick={() => form.reset()}
                            disabled={submitting || pristine}
                        >
                            Reset
                        </button>
                    </div>
                    <pre>
                        LOG MESSAGES: error from backend
                        <br/>
                        </pre>
                </form>
            </Styles>
        );

}


export default VerifyAccessPanel;


